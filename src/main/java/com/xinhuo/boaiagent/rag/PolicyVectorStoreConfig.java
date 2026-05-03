package com.xinhuo.boaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

        // 加载政策 Markdown 文档
        List<Document> documentList = policyDocumentLoader.loadMarkdowns();

        // 自定义分词切分
        List<Document> splitDocumentList = myTokenTextSplitter.splitCustomized(documentList);

        // 关键词元数据增强，提升召回准确率
        List<Document> enrichedDocumentList = myKeywordEnricher.enrichDocuments(splitDocumentList);

        // 写入本地向量库
        simpleVectorStore.add(enrichedDocumentList);

        return simpleVectorStore;
    }
}
