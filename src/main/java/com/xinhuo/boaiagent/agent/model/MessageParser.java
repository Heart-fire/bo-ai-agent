package com.xinhuo.boaiagent.agent.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息解析器，用于从工具返回结果中提取结构化信息
 */
@Slf4j
public class MessageParser {

    // URL 匹配正则
    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?"
    );

    // 图片文件扩展名
    private static final List<String> IMAGE_EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg"
    );

    /**
     * 解析工具返回结果，提取结构化内容
     *
     * @param toolResult 工具返回的原始结果
     * @return 解析后的结构化内容
     */
    public static StructuredMessage.ResultContent parseToolResult(String toolResult) {
        if (StrUtil.isBlank(toolResult)) {
            return StructuredMessage.ResultContent.builder().build();
        }

        StructuredMessage.ResultContent.ResultContentBuilder builder = StructuredMessage.ResultContent.builder();

        // 1. 尝试解析 JSON 格式的结果
        if (JSONUtil.isTypeJSON(toolResult)) {
            return parseJsonResult(toolResult);
        }

        // 2. 解析文本内容，提取图片 URL、普通链接等
        builder.text(extractTextContent(toolResult));
        builder.images(extractImageUrls(toolResult));
        builder.links(extractLinks(toolResult));

        return builder.build();
    }

    /**
     * 解析 JSON 格式的结果
     */
    private static StructuredMessage.ResultContent parseJsonResult(String jsonStr) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
            StructuredMessage.ResultContent.ResultContentBuilder builder =
                    StructuredMessage.ResultContent.builder();

            // 提取文本
            if (jsonObject.containsKey("text") || jsonObject.containsKey("content")) {
                builder.text(jsonObject.getStr("text", jsonObject.getStr("content")));
            }

            // 提取图片列表
            if (jsonObject.containsKey("images")) {
                List<StructuredMessage.ImageItem> images = new ArrayList<>();
                jsonObject.getJSONArray("images").forEach(img -> {
                    if (img instanceof String url) {
                        images.add(StructuredMessage.ImageItem.builder()
                                .url(url)
                                .description("下载的图片")
                                .build());
                    }
                });
                builder.images(images);
            }

            // 提取链接列表
            if (jsonObject.containsKey("links")) {
                List<StructuredMessage.LinkItem> links = new ArrayList<>();
                jsonObject.getJSONArray("links").forEach(link -> {
                    if (link instanceof String url) {
                        links.add(StructuredMessage.LinkItem.builder()
                                .url(url)
                                .title(url)
                                .build());
                    }
                });
                builder.links(links);
            }

            return builder.build();
        } catch (Exception e) {
            log.warn("Failed to parse JSON result: {}", e.getMessage());
            return parseToolResult(jsonStr); // 回退到普通解析
        }
    }

    /**
     * 提取纯文本内容（去除 URL 等结构化数据）
     */
    private static String extractTextContent(String content) {
        // 移除图片 URL 行
        String text = content.replaceAll("(?m)^图片已保存到：.*$", "");
        text = text.replaceAll("(?m)^图片下载完成：.*$", "");
        text = text.replaceAll("(?m)^下载的图片：.*$", "");

        // 清理多余空行
        text = text.replaceAll("\\n{3,}", "\n\n");

        return text.trim();
    }

    /**
     * 从文本中提取图片 URL
     */
    private static List<StructuredMessage.ImageItem> extractImageUrls(String content) {
        List<StructuredMessage.ImageItem> images = new ArrayList<>();

        // 匹配所有 URL
        Matcher matcher = URL_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            if (isImageUrl(url)) {
                images.add(StructuredMessage.ImageItem.builder()
                        .url(url)
                        .description("找到的图片")
                        .build());
            }
        }

        // 查找图片文件路径
        Pattern imageFilePattern = Pattern.compile("(图片已保存到：|下载的图片：)\\s*(.*?\\.(jpg|jpeg|png|gif|webp|bmp))", Pattern.CASE_INSENSITIVE);
        Matcher fileMatcher = imageFilePattern.matcher(content);
        while (fileMatcher.find()) {
            String filePath = fileMatcher.group(2).trim();
            images.add(StructuredMessage.ImageItem.builder()
                    .url(filePath)
                    .description("下载的图片")
                    .build());
        }

        return images;
    }

    /**
     * 从文本中提取链接
     */
    private static List<StructuredMessage.LinkItem> extractLinks(String content) {
        List<StructuredMessage.LinkItem> links = new ArrayList<>();

        Matcher matcher = URL_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            // 跳过图片链接
            if (!isImageUrl(url)) {
                // 尝试提取链接附近的文字作为标题
                String title = extractLinkTitle(content, url);
                links.add(StructuredMessage.LinkItem.builder()
                        .url(url)
                        .title(title)
                        .build());
            }
        }

        return links;
    }

    /**
     * 判断是否为图片 URL
     */
    private static boolean isImageUrl(String url) {
        String lowerUrl = url.toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(ext -> lowerUrl.contains(ext));
    }

    /**
     * 提取链接标题（URL 前面的文字）
     */
    private static String extractLinkTitle(String content, String url) {
        int urlIndex = content.indexOf(url);
        if (urlIndex == -1) return url;

        // 获取 URL 前面 50 个字符
        int start = Math.max(0, urlIndex - 50);
        String beforeText = content.substring(start, urlIndex);

        // 提取最后一句话或标题
        String[] lines = beforeText.split("\\n");
        if (lines.length > 0) {
            String lastLine = lines[lines.length - 1].trim();
            if (StrUtil.isNotBlank(lastLine)) {
                // 限制标题长度
                return lastLine.length() > 50 ? lastLine.substring(0, 50) + "..." : lastLine;
            }
        }

        return "参考链接";
    }

    /**
     * 生成智能总结（结构化）
     *
     * @param allResults     所有步骤结果
     * @param usedTools      使用的工具列表
     * @param summaryPrompt  总结提示词
     * @return 结构化的总结信息
     */
    public static StructuredMessage.SummaryInfo parseSummary(
            List<String> allResults,
            List<String> usedTools,
            String summaryPrompt) {

        StructuredMessage.SummaryInfo.SummaryInfoBuilder builder =
                StructuredMessage.SummaryInfo.builder();

        builder.totalSteps(allResults.size());
        builder.toolsUsed(usedTools);

        // 从结果中提取关键信息
        List<String> keyPoints = extractKeyPoints(allResults);
        builder.keyPoints(keyPoints);

        // 生成核心结论
        String conclusion = generateConclusion(allResults);
        builder.conclusion(conclusion);

        // 提取资源（图片、链接）
        List<StructuredMessage.ResourceItem> resources = extractResources(allResults);
        builder.resources(resources);

        return builder.build();
    }

    /**
     * 从所有结果中提取关键点
     */
    private static List<String> extractKeyPoints(List<String> results) {
        List<String> keyPoints = new ArrayList<>();

        for (String result : results) {
            // 提取包含"找到"、"推荐"、"结果"等关键词的句子
            String[] sentences = result.split("[。！？\\n]");
            for (String sentence : sentences) {
                String trimmed = sentence.trim();
                if (trimmed.length() > 10 && trimmed.length() < 100 &&
                        (trimmed.contains("推荐") || trimmed.contains("找到") ||
                         trimmed.contains("发现") || trimmed.contains("结果"))) {
                    keyPoints.add(trimmed);
                }
            }

            // 限制关键点数量
            if (keyPoints.size() >= 5) break;
        }

        return keyPoints;
    }

    /**
     * 生成核心结论
     */
    private static String generateConclusion(List<String> results) {
        if (CollUtil.isEmpty(results)) {
            return "任务已完成";
        }

        // 简单实现：使用第一个结果的开头作为结论
        String firstResult = results.get(0);
        if (StrUtil.isNotBlank(firstResult)) {
            // 提取第一句话
            String[] sentences = firstResult.split("[。！？\\n]");
            if (sentences.length > 0) {
                String conclusion = sentences[0].trim();
                return conclusion.length() > 100 ? conclusion.substring(0, 100) + "..." : conclusion;
            }
        }

        return "已为您找到相关信息";
    }

    /**
     * 提取所有资源（图片、链接）
     */
    private static List<StructuredMessage.ResourceItem> extractResources(List<String> results) {
        List<StructuredMessage.ResourceItem> resources = new ArrayList<>();

        for (String result : results) {
            // 提取图片
            List<StructuredMessage.ImageItem> images = extractImageUrls(result);
            for (StructuredMessage.ImageItem image : images) {
                resources.add(StructuredMessage.ResourceItem.builder()
                        .type("image")
                        .data(image)
                        .build());
            }

            // 提取链接
            List<StructuredMessage.LinkItem> links = extractLinks(result);
            for (StructuredMessage.LinkItem link : links) {
                resources.add(StructuredMessage.ResourceItem.builder()
                        .type("link")
                        .data(link)
                        .build());
            }
        }

        return resources;
    }
}
