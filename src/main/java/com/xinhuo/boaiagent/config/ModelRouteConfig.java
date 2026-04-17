package com.xinhuo.boaiagent.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ModelRouteConfig {

    /**
     * 默认 ChatModel：标记 DashScope 为 @Primary
     * 所有未指定 @Qualifier 的注入点都会使用 DashScope
     */
    @Primary
    @Bean
    public ChatModel primaryChatModel(@Qualifier("dashScopeChatModel") ChatModel dashscopeChatModel) {
        return dashscopeChatModel;
    }

    /**
     * 模型路由表：modelName -> ChatModel Bean
     * 前端传入 model 参数，由此映射到具体的 ChatModel
     */
    @Bean
    public Map<String, ChatModel> modelRoute(
            @Qualifier("dashScopeChatModel") ChatModel dashscopeChatModel,
            @Qualifier("deepSeekChatModel") ChatModel deepSeekChatModel
    ) {
        Map<String, ChatModel> route = new HashMap<>();
        route.put("dashscope", dashscopeChatModel);
        route.put("deepseek", deepSeekChatModel);
        return route;
    }
}
