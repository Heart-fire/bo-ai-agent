package com.xinhuo.boaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.xinhuo.boaiagent.agent.model.AgentState;
import com.xinhuo.boaiagent.agent.model.SseClosedException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 代理状态
    private AgentState state = AgentState.IDLE;

    // 执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 7;

    // LLM 大模型
    private ChatClient chatClient;

    // Memory 记忆
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 1、基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        // 2、执行，更改状态
        this.state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();
        try {
            // 执行循环
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 3、清理资源
            this.cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        // 不设超时（0=无限），由服务端任务完成后主动关闭连接
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            // 1、基础校验
            try {
                if (this.state != AgentState.IDLE) {
                    sendSse(sseEmitter, "错误：无法从状态运行代理：" + this.state);
                    sseEmitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    sendSse(sseEmitter, "错误：不能使用空提示词运行代理");
                    sseEmitter.complete();
                    return;
                }
            } catch (SseClosedException e) {
                sseEmitter.completeWithError(e.getCause());
                return;
            }
            // 2、执行，更改状态
            this.state = AgentState.RUNNING;
            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));
            // 保存结果列表
            List<String> results = new ArrayList<>();
            try {

                // 执行循环
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("执行步骤： {}/{}", stepNumber, maxSteps);
                    // 流式单步执行，内部已实时推送 SSE 事件
                    String stepResult = stepStream(sseEmitter);
                    results.add(stepResult);
                }

                // 检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    sendSse(sseEmitter, "{\"type\":\"info\",\"message\":\"执行结束：达到最大步骤（" + maxSteps + "）\"}");
                }
                // 任务完成后生成总结
                if (state == AgentState.FINISHED && !results.isEmpty()) {
                    try {
                        log.info("开始生成总结，共 {} 个步骤结果", results.size());
                        String summary = generateSummary(results);
                        log.info("总结生成完成，长度: {} 字符", summary != null ? summary.length() : 0);
                        // 总结已经是 JSON 格式，直接发送
                        sendSse(sseEmitter, summary);
                        log.info("总结已发送到前端");
                    } catch (SseClosedException e) {
                        log.warn("SSE连接已关闭，总结未发送");
                        throw e;
                    } catch (Exception e) {
                        log.error("生成总结失败", e);
                        sendSse(sseEmitter, "{\"type\":\"error\",\"message\":\"总结生成失败: " + e.getMessage() + "\"}");
                    }

                    // 流式输出总结文本（像聊天 AI 一样逐字输出）
                    streamSummary(sseEmitter, results);
                }
                // 正常完成
                log.info("SSE 连接即将完成，总步骤数: {}", currentStep);
                sseEmitter.complete();
                log.info("SSE 连接已完成");
            } catch (SseClosedException e) {
                log.warn("SSE连接已关闭，停止发送");
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("error executing agent", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IllegalStateException | IOException ex) {
                    log.warn("SSE连接已关闭，无法发送错误信息");
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                // 3、清理资源
                this.cleanup();
            }
        });

        // 设置超时回调
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });
        // 设置完成回调
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });
        return sseEmitter;
    }

    /**
     * 定义单个步骤
     *
     * @return
     */
    public abstract String step();

    /**
     * 流式执行单步（子类可重写以实现逐步推送 SSE 事件）
     * 默认实现退化为调用 step()
     *
     * @param emitter SSE 发射器，用于实时推送事件
     * @return 步骤执行结果
     */
    public String stepStream(SseEmitter emitter) {
        return step();
    }

    /**
     * 安全发送 SSE 事件，统一处理 IOException / IllegalStateException
     *
     * @param emitter SSE 发射器
     * @param data    要发送的数据
     */
    protected void sendSse(SseEmitter emitter, Object data) {
        try {
            emitter.send(data);
        } catch (IllegalStateException | IOException e) {
            throw new SseClosedException(e);
        }
    }

    /**
     * 生成任务总结（子类可以重写以自定义总结格式）
     *
     * @param results 所有步骤的结果
     * @return 总结内容
     */
    protected String generateSummary(List<String> results) {
        // 子类可以重写此方法，调用 AI 生成更智能的总结
        return "任务已完成，共执行 " + results.size() + " 个步骤。";
    }

    /**
     * 流式输出总结文本（子类可重写以使用 LLM 流式输出）
     * 在卡片总结发送之后调用，用于像聊天 AI 一样逐字流式输出总结内容
     *
     * @param emitter SSE 发射器
     * @param results 所有步骤的结果
     */
    protected void streamSummary(SseEmitter emitter, List<String> results) {
        // 默认空实现，子类可重写
    }

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }
}
