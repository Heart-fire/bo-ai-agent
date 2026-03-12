package com.xinhuo.boaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class PgVectorVectorStoreConfigTest {
    @Resource
    VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {

        List<Document> documents = List.of(
                new Document("ChatGPT是什么？", Map.of("meta1", "meta1")),
                new Document("恋爱的过程"),
                new Document("关于婚后", Map.of("meta2", "meta2")));

        // Add the documents to PGVector
        pgVectorVectorStore.add(documents);

        // Retrieve documents similar to a query
        List<Document> results = this.pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());

    }
}