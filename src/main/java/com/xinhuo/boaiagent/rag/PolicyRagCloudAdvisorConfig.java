package com.xinhuo.boaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 云端 RAG Advisor 配置
 */
@Configuration
@Profile("cloud-rag")
@Slf4j
public class PolicyRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashscopeApiKey;

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(dashscopeApiKey)
                .build();
    }

    /**
     * 云端政策知识库
     * DashScopeApi 由 spring-ai-alibaba-starter-dashscope 自动配置，无需手动创建
     */
    @Bean
    public Advisor policyRagCloudAdvisor(DashScopeApi dashScopeApi) {
        final String KNOWLEDGE_INDEX = "政策通";
//        log.info("初始化云端 RAG Advisor，知识库索引: {}", KNOWLEDGE_INDEX);
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(
                dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .indexName(KNOWLEDGE_INDEX)
                        .build()
        );
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}
