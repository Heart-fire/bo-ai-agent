package com.xinhuo.boaiagent.agent;

import cn.hutool.json.JSONUtil;
import com.xinhuo.boaiagent.agent.model.SseClosedException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 行动执行结果
     */
    public abstract String act();


    /**
     * 执行单个步骤：思考和行动
     *
     * @return 步骤执行结果（JSON 数组格式，用于 BaseAgent.runStream 解析）
     */
    @Override
    public String step() {
        try {
            // 先思考
            boolean shouldAct = think();

            // 生成思考消息
            Map<String,Object> thoughtData = Map.of(
                    "step", getCurrentStep(),
                    "content", getThoughtContent()
            );

            Map<String,Object> thoughtMessage = Map.of(
                    "type", "thought",
                    "data", thoughtData
            );

            String thoughtJson = JSONUtil.toJsonStr(thoughtMessage);

            // 如果不需要行动，只返回思考
            if (!shouldAct) {
                // 返回单元素数组
                List<String> messages = new ArrayList<>();
                messages.add(thoughtJson);
                return JSONUtil.toJsonStr(messages);
            }

            // 执行动作
            String actionJson = act(); // act() 内部要保证返回 {"type":"action","data":...}

            // 返回两条消息的 JSON 数组
            List<String> messages = new ArrayList<>();
            messages.add(thoughtJson);
            messages.add(actionJson);
            return JSONUtil.toJsonStr(messages);

        } catch (Exception e) {
            log.error("步骤执行失败", e);
            Map<String,Object> errorMessage = Map.of(
                    "type", "error",
                    "message", "步骤执行失败：" + e.getMessage()
            );
            // 错误也返回数组格式
            List<String> messages = new ArrayList<>();
            messages.add(JSONUtil.toJsonStr(errorMessage));
            return JSONUtil.toJsonStr(messages);
        }
    }

    /**
     * 获取思考内容（子类可以重写）
     *
     * @return 思考内容
     */
    protected String getThoughtContent() {
        return "";
    }

    /**
     * 流式执行单步：think 完成后立即推送 thought，再执行 act 推送 action
     * @return 步骤执行结果
     */
    @Override
    public String stepStream(SseEmitter emitter) {
        try {
            // 1. 先思考
            boolean shouldAct = think();

            // 2. 立即推送 thought 事件（不等 act）
            Map<String, Object> thoughtData = Map.of(
                    "step", getCurrentStep(),
                    "content", getThoughtContent()
            );
            Map<String, Object> thoughtMessage = Map.of(
                    "type", "thought",
                    "data", thoughtData
            );
            sendSse(emitter, JSONUtil.toJsonStr(thoughtMessage));

            // 3. 如果不需要行动，返回 thought
            if (!shouldAct) {
                List<String> messages = new ArrayList<>();
                messages.add(JSONUtil.toJsonStr(thoughtMessage));
                return JSONUtil.toJsonStr(messages);
            }

            // 4. 执行动作
            String actionJson = act();

            // 5. 推送 action 事件
            sendSse(emitter, actionJson);

            // 返回完整结果（用于 generateSummary）
            List<String> messages = new ArrayList<>();
            messages.add(JSONUtil.toJsonStr(thoughtMessage));
            messages.add(actionJson);
            return JSONUtil.toJsonStr(messages);

        } catch (SseClosedException e) {
            throw e; // 向上传播，由 BaseAgent 统一处理
        } catch (Exception e) {
            log.error("步骤执行失败", e);
            Map<String, Object> errorMessage = Map.of(
                    "type", "error",
                    "message", "步骤执行失败：" + e.getMessage()
            );
            sendSse(emitter, JSONUtil.toJsonStr(errorMessage));
            List<String> messages = new ArrayList<>();
            messages.add(JSONUtil.toJsonStr(errorMessage));
            return JSONUtil.toJsonStr(messages);
        }
    }

}