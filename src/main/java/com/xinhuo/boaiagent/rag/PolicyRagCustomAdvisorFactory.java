package com.xinhuo.boaiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 政策 RAG 自定义检索增强 Advisor 工厂
 *
 * 支持按 category 元数据过滤，精准召回对应分类的政策文档：
 *   - "social"     → 社保、医保相关
 *   - "traffic"    → 进京证、车辆通行相关
 *   - "residence"  → 落户、居住证相关
 *   - "medical"    → 医疗、健康相关
 *   - "general"    → 通用政策
 */
public class PolicyRagCustomAdvisorFactory {

    /**
     * 创建带 category 过滤的 RAG Advisor
     *
     * @param vectorStore 向量存储（SimpleVectorStore 或 PgVectorStore）
     * @param category    文档分类标签，与 PolicyDocumentLoader 中提取的 category 一致
     * @return 配置好的 RetrievalAugmentationAdvisor
     */
    public static Advisor createPolicyRagCustomAdvisor(VectorStore vectorStore, String category) {
        // 按 category 元数据过滤
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("category", category)
                .build();

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)   // 分类过滤
                .similarityThreshold(0.6)       // 政策文档语义相似度阈值（略低于通用场景，提升召回率）
                .topK(5)                        // 返回最相关的 5 段文档切片
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                // 知识库无结果时的兜底回复
                .queryAugmenter(PolicyContextualQueryAugmenterFactory.createInstance())
                .build();
    }

    /**
     * 不带分类过滤的全库检索（适用于用户问题跨多个政策类别时）
     */
    public static Advisor createPolicyRagAdvisorWithoutFilter(VectorStore vectorStore) {
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.6)
                .topK(5)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(PolicyContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}
