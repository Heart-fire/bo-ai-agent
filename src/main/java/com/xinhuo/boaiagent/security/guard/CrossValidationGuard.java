package com.xinhuo.boaiagent.security.guard;

import com.xinhuo.boaiagent.security.model.ThreatLevel;
import com.xinhuo.boaiagent.security.model.ThreatReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Layer3: 多模型交叉验证
 * 并行调用 DashScope + DeepSeek 两个模型，对同一输入做安全性判断，对比结果
 */
@Slf4j
public class CrossValidationGuard {

    private static final String GUARD_NAME = "CrossValidationGuard";

    private static final String SAFETY_QUESTION_PROMPT = """
            分析以下用户输入，判断它是否安全，可以被北京政策问答 AI 助手处理。
            只回答 "SAFE" 或 "UNSAFE"，然后简要说明理由。

            用户输入："%s"
            """;

    private final ChatClient primaryChatClient;
    private final ChatClient secondaryChatClient;
    private final boolean enabled;
    private final int timeoutSeconds;

    public CrossValidationGuard(ChatModel primaryModel, ChatModel secondaryModel,
                                boolean enabled, int timeoutSeconds) {
        this.primaryChatClient = ChatClient.builder(primaryModel).build();
        this.secondaryChatClient = ChatClient.builder(secondaryModel).build();
        this.enabled = enabled;
        this.timeoutSeconds = timeoutSeconds;
    }

    public ThreatReport check(String input) {
        if (!enabled || input == null || input.isBlank()) {
            return ThreatReport.safe(GUARD_NAME);
        }

        try {
            // 并行调用两个模型
            CompletableFuture<Boolean> primaryFuture = CompletableFuture.supplyAsync(() ->
                    isInputSafe(primaryChatClient, input));
            CompletableFuture<Boolean> secondaryFuture = CompletableFuture.supplyAsync(() ->
                    isInputSafe(secondaryChatClient, input));

            boolean primarySafe = primaryFuture.get(timeoutSeconds, TimeUnit.SECONDS);
            boolean secondarySafe = secondaryFuture.get(timeoutSeconds, TimeUnit.SECONDS);

            log.info("交叉验证: primary={}, secondary={}, input={}",
                    primarySafe ? "SAFE" : "UNSAFE",
                    secondarySafe ? "SAFE" : "UNSAFE",
                    truncate(input));

            // 两个都认为安全 → 通过
            if (primarySafe && secondarySafe) {
                return ThreatReport.safe(GUARD_NAME);
            }

            // 两个都认为不安全 → 恶意
            if (!primarySafe && !secondarySafe) {
                return ThreatReport.builder()
                        .threatLevel(ThreatLevel.MALICIOUS)
                        .guardName(GUARD_NAME)
                        .reason("两个模型均判定输入不安全")
                        .originalInput(input)
                        .confidence(0.9)
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // 意见不一 → 可疑，倾向阻断
            return ThreatReport.builder()
                    .threatLevel(ThreatLevel.SUSPICIOUS)
                    .guardName(GUARD_NAME)
                    .reason("模型判定不一致: 主模型=" + (primarySafe ? "安全" : "不安全")
                            + ", 辅模型=" + (secondarySafe ? "安全" : "不安全"))
                    .originalInput(input)
                    .confidence(0.6)
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            // fail-open: 交叉验证失败时不阻断请求
            log.error("交叉验证失败 (timeout or error), allowing input: {}", e.getMessage());
            return ThreatReport.safe(GUARD_NAME);
        }
    }

    private boolean isInputSafe(ChatClient chatClient, String input) {
        try {
            String response = chatClient.prompt()
                    .user(SAFETY_QUESTION_PROMPT.formatted(input))
                    .call()
                    .content();
            return isResponseSafe(response);
        } catch (Exception e) {
            log.warn("模型安全检查失败: {}", e.getMessage());
            return true; // 单个模型失败时默认安全，不影响整体判定
        }
    }

    private boolean isResponseSafe(String response) {
        if (response == null || response.isBlank()) {
            return true;
        }
        String trimmed = response.trim().toUpperCase();
        // 判断模型回答是否以 SAFE 开头，且不包含 UNSAFE
        if (trimmed.startsWith("SAFE") && !trimmed.contains("UNSAFE")) {
            return true;
        }
        // 中文"安全"也算安全
        return trimmed.contains("安全") && !trimmed.contains("不安全");
    }

    private String truncate(String s) {
        return s.length() <= 80 ? s : s.substring(0, 80) + "...";
    }
}
