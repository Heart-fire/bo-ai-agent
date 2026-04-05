package com.xinhuo.boaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 北京政策文档加载器
 *
 * 文件命名规范（影响元数据提取）：
 *   {文档描述}-{category}.md
 *   示例：
 *     北京社保手册-social.md        → category=social
 *     进京证办理指南-traffic.md      → category=traffic
 *     北京积分落户政策-residence.md  → category=residence
 *     医保报销说明-medical.md        → category=medical
 *
 * 若文件名不含 "-" 分隔符，category 默认为 "general"。
 */
@Component
@Slf4j
public class PolicyDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public PolicyDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 classpath:document/ 下所有 Markdown 文档，并附加政策类元数据
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/**/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) {
                    continue;
                }

                // 从文件名中提取 category：取最后一个 "-" 到 ".md" 之间的字符串
                // 例如 "北京社保手册-social.md" → "social"
                String category = extractCategory(filename);

                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("category", category)
                        // 保留 source 字段，方便 RAG 溯源时展示来源文件
                        .withAdditionalMetadata("source", "北京市政策知识库")
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                List<Document> docs = reader.get();
                allDocuments.addAll(docs);
                log.info("已加载政策文档: {} | category={} | 切片数={}", filename, category, docs.size());
            }
        } catch (IOException e) {
            log.error("政策文档加载失败", e);
        }
        log.info("政策文档加载完成，共 {} 个文档切片", allDocuments.size());
        return allDocuments;
    }

    /**
     * 从文件名提取分类标签
     * 规则：取最后一个 "-" 之后、".md" 之前的部分
     * 无法提取时返回 "general"
     */
    private String extractCategory(String filename) {
        try {
            // 去掉 .md 后缀
            String nameWithoutExt = filename.endsWith(".md")
                    ? filename.substring(0, filename.length() - 3)
                    : filename;
            int lastDash = nameWithoutExt.lastIndexOf("-");
            if (lastDash >= 0 && lastDash < nameWithoutExt.length() - 1) {
                return nameWithoutExt.substring(lastDash + 1).trim();
            }
        } catch (Exception e) {
            log.warn("文件名 category 提取失败: {}", filename);
        }
        return "general";
    }
}
