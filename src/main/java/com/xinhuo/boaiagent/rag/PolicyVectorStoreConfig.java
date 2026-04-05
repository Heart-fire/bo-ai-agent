package com.xinhuo.boaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 北京政策知识库向量数据库配置
 * 使用基于内存的 SimpleVectorStore，适合开发/演示场景
 * 生产环境可切换为 pgVectorVectorStore（已在 PgVectorVectorStoreConfig 中配置）
 */
@Configuration
public class PolicyVectorStoreConfig {

    @Resource
    private PolicyDocumentLoader policyDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore policyVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        // 1. 加载政策 Markdown 文档
        List<Document> documentList = policyDocumentLoader.loadMarkdowns();

        // 2. 可选：自定义分词切分（文档较长时开启）
        // List<Document> splitDocumentList = myTokenTextSplitter.splitCustomized(documentList);

        // 3. 关键词元数据增强（提升召回准确率）
        List<Document> enrichedDocumentList = myKeywordEnricher.enrichDocuments(documentList);

        // 4. 写入向量库
        simpleVectorStore.add(enrichedDocumentList);

        return simpleVectorStore;
    }
}
