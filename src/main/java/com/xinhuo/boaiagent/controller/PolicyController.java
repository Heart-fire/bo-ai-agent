package com.xinhuo.boaiagent.controller;

import com.xinhuo.boaiagent.agent.ResearchAgent;
import com.xinhuo.boaiagent.app.PolicyApp;
import com.xinhuo.boaiagent.security.guard.InputGuard;
import com.xinhuo.boaiagent.security.guard.InputGuardResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AI 接口控制器
 * - /ai/policy_app/** ：北京政策问答（纯聊天 + RAG）
 * - /ai/research/**   ：公开信息采集研究员（自主 Agent）
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class PolicyController {

    @Resource
    private PolicyApp policyApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private Map<String, ChatModel> modelRoute;

    @Resource(name = "inputGuard")
    @Nullable
    private InputGuard inputGuard;

    /**
     * 根据参数选择 ChatModel，找不到则 fallback 到 DashScope
     */
    private ChatModel resolveModel(String model) {
        if (model == null || model.isBlank()) {
            return dashscopeChatModel;
        }
        return modelRoute.getOrDefault(model.toLowerCase(), dashscopeChatModel);
    }



    /**
     * 政策问答 - 流式输出（SSE）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
    @GetMapping(value = "/policy_app/chat/sse")
        public SseEmitter doChatWithPolicyAppBySse(String message, String chatId, String model) {
        // chatId 为空时自动生成，避免所有请求共用 "chat:memory:null"
        if (chatId == null || chatId.isBlank()) {
            chatId = UUID.randomUUID().toString();
        }
        log.debug("Policy chat: chatId={}", chatId);
        SseEmitter sseEmitter = new SseEmitter(180_000L); // 3 分钟超时

        // 安全防护检查
        InputGuardResult guardResult = checkSecurity(message);
        if (guardResult.isBlocked()) {
            sendBlockMessage(sseEmitter, guardResult.getMessage());
            return sseEmitter;
        }

        ChatModel selectedModel = resolveModel(model);

        // 用于保证 Subscription 只取消一次（onTimeout / onError / onCompletion 可能并发触发）
        AtomicBoolean disposed = new AtomicBoolean(false);
        // AtomicReference 打破 lambda 与 subscribe 返回值的循环依赖
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();

        // 订阅 Flux 流，subscribe() 返回后将 Disposable 存入 ref
        String finalChatId3 = chatId;
        Disposable subscription = policyApp.doChatByStream(message, chatId, selectedModel)
                .subscribe(
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException | IllegalStateException e) {
                                // 客户端已断开，取消订阅，停止消费 Flux
                                cancelSubscription(subscriptionRef.get(), disposed);
                            }
                        },
                        error -> {
                            log.error("Flux 流异常终止: {}", error.getMessage());
                            sseEmitter.completeWithError(error);
                        },
                        () -> {
                            log.debug("Flux 流正常完成, chatId={}", finalChatId3);
                            sseEmitter.complete();
                        }
                );
        subscriptionRef.set(subscription);

        // 超时回调：3 分钟超时触发，取消订阅
        String finalChatId = chatId;
        sseEmitter.onTimeout(() -> {
            log.warn("SSE 连接超时, chatId={}", finalChatId);
            cancelSubscription(subscriptionRef.get(), disposed);
        });

        // 客户端主动断开回调
        String finalChatId1 = chatId;
        sseEmitter.onError(throwable -> {
            log.warn("SSE 连接异常断开, chatId={}: {}", finalChatId1, throwable.getMessage());
            cancelSubscription(subscriptionRef.get(), disposed);
        });

        // 完成回调（无论正常/异常最终都会触发，兜底清理）
        String finalChatId2 = chatId;
        sseEmitter.onCompletion(() -> {
            log.debug("SSE 连接关闭, chatId={}", finalChatId2);
            cancelSubscription(subscriptionRef.get(), disposed);
        });

        return sseEmitter;
    }


    /**
     * 研究员 Agent - 流式执行（SSE）
     * Agent 自主规划：搜索 → 抓取 → 整理 → 生成 PDF 报告
     */
    @GetMapping("/research/chat")
    public SseEmitter doChatWithResearchAgent(String message, String model) {
        SseEmitter sseEmitter = new SseEmitter(0L);

        // 安全防护检查
        InputGuardResult guardResult = checkSecurity(message);
        if (guardResult.isBlocked()) {
            String blockJson = "{\"type\":\"error\",\"message\":\"" + guardResult.getMessage() + "\"}";
            sendBlockMessage(sseEmitter, blockJson);
            return sseEmitter;
        }

        ChatModel selectedModel = resolveModel(model);
        ResearchAgent researchAgent = new ResearchAgent(allTools, selectedModel);
        return researchAgent.runStream(message);
    }


    /**
     * 文件下载端点 - 提供 tmp/pdf/ 目录下的文件下载
     */
    @GetMapping("/files/{*filename}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), "tmp", "pdf", filename);
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"download.pdf\"; filename*=UTF-8''" + encodedName)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * 政策问答 - RAG 增强对话（查询重写 + 知识库召回）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
//    @GetMapping("/policy_app/chat/rag")
//    public String doChatWithPolicyAppRag(String message, String chatId) {
//        return policyApp.doChatWithRag(message, chatId);
//    }

    /**
     * 政策问答 - 结构化输出（政策摘要报告）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
//    @GetMapping("/policy_app/chat/report")
//    public Object doChatWithPolicyAppReport(String message, String chatId) {
//        return policyApp.doChatWithReport(message, chatId);
//    }


    /**
     * 流式执行（SSE）
     * 使用 ResearchAgent 作为底层实现
     *
     * @param message 用户问题
     */
//    @GetMapping("/manus/chat")
//    public SseEmitter doChatWithManus(String message) {
//        ResearchAgent researchAgent = new ResearchAgent(allTools, dashscopeChatModel);
//        return researchAgent.runStream(message);
//    }

    // ==================== 安全防护辅助方法 ====================

    /**
     * 执行安全检查，guard 未启用时直接返回安全结果
     */
    private InputGuardResult checkSecurity(String message) {
        if (inputGuard == null) {
            return InputGuardResult.safe();
        }
        return inputGuard.check(message);
    }

    /**
     * 通过 SSE 发送安全拦截消息并关闭连接
     */
    private void sendBlockMessage(SseEmitter sseEmitter, String message) {
        try {
            sseEmitter.send(message);
            sseEmitter.complete();
        } catch (IOException e) {
            log.warn("Failed to send block message via SSE", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 安全取消 Flux 订阅，保证只取消一次
     */
    private void cancelSubscription(Disposable subscription, AtomicBoolean disposed) {
        if (subscription == null) return;
        if (disposed.compareAndSet(false, true) && !subscription.isDisposed()) {
            subscription.dispose();
            log.debug("Flux 订阅已取消");
        }
    }
}
