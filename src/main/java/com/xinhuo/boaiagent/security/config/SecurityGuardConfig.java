package com.xinhuo.boaiagent.security.config;

import com.xinhuo.boaiagent.security.guard.CrossValidationGuard;
import com.xinhuo.boaiagent.security.guard.InputGuard;
import com.xinhuo.boaiagent.security.guard.IntentRecognitionGuard;
import com.xinhuo.boaiagent.security.guard.KeywordBlacklistGuard;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全防护配置类
 * 通过 security.guard.enabled 控制整体开关，各层独立开关
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(SecurityGuardConfig.KeywordGuardProperties.class)
public class SecurityGuardConfig {

    /**
     * 关键词防护配置属性
     * 用 @ConfigurationProperties 绑定 YAML 列表
     */
    @Data
    @ConfigurationProperties(prefix = "security.guard.keyword")
    public static class KeywordGuardProperties {
        private List<String> patterns = new ArrayList<>();
        private boolean enabled = true;
    }

    @Bean
    @ConditionalOnProperty(name = "security.guard.enabled", havingValue = "true")
    public KeywordBlacklistGuard keywordBlacklistGuard(KeywordGuardProperties props) {
        log.info("用 {} 模式初始化 KeywordBlacklistGuard", props.getPatterns().size());
        return new KeywordBlacklistGuard(props.getPatterns(), props.isEnabled());
    }

    @Bean
    @ConditionalOnProperty(name = "security.guard.enabled", havingValue = "true")
    public IntentRecognitionGuard intentRecognitionGuard(
            @Qualifier("deepSeekChatModel") ChatModel deepSeekChatModel,
            @Value("${security.guard.intent.enabled:true}") boolean enabled,
            @Value("${security.guard.intent.threshold:0.7}") double threshold) {
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel).build();
        log.info("初始化阈值为 {} 的 IntentRecognitionGuard", threshold);
        return new IntentRecognitionGuard(chatClient, enabled, threshold);
    }

    @Bean
    @ConditionalOnProperty(name = "security.guard.enabled", havingValue = "true")
    public CrossValidationGuard crossValidationGuard(
            @Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel,
            @Qualifier("deepSeekChatModel") ChatModel deepSeekChatModel,
            @Value("${security.guard.cross-validation.enabled:true}") boolean enabled,
            @Value("${security.guard.cross-validation.timeout-seconds:30}") int timeoutSeconds) {
        log.info("使用 timeout={}s 初始化 CrossValidationGuard", timeoutSeconds);
        return new CrossValidationGuard(dashScopeChatModel, deepSeekChatModel, enabled, timeoutSeconds);
    }

    @Bean(name = "inputGuard")
    @ConditionalOnProperty(name = "security.guard.enabled", havingValue = "true")
    public InputGuard inputGuard(KeywordBlacklistGuard keywordGuard,
                                  IntentRecognitionGuard intentGuard,
                                  CrossValidationGuard crossValidationGuard,
                                  @Value("${security.guard.enabled}") boolean globalEnabled) {
        log.info("初始化 InputGuard （globalEnabled={}）", globalEnabled);
        return new InputGuard(keywordGuard, intentGuard, crossValidationGuard, globalEnabled);
    }
}
