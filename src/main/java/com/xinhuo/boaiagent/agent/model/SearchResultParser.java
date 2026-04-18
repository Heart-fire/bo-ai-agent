package com.xinhuo.boaiagent.agent.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索结果解析器 - 将搜索结果解析为前端可直接展示的卡片格式
 */
@Slf4j
public class SearchResultParser {

    // 匹配 【数字】标题 格式
    private static final Pattern ITEM_TITLE_PATTERN = Pattern.compile("【(\\d+)】([^\\n【]+)");

    // 匹配 "标题：" 格式
    private static final Pattern TITLE_COLON_PATTERN = Pattern.compile("^([\\u4e00-\\u9fa5a-zA-Z0-9\\s]+)：");

    // 匹配 "链接：" 后的 URL
    private static final Pattern LINK_URL_PATTERN = Pattern.compile("链接：\\s*(https?://[^\\s\\n\"'}\\\\]+)");

    // 匹配 URL
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s\\n\"'}\\\\]+");

    // 匹配图片文件路径
    private static final Pattern IMAGE_PATH_PATTERN = Pattern.compile(
            "(图片已保存到：|下载的图片：|图片：)\\s*(.*?\\.(jpg|jpeg|png|gif|webp|bmp))",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 解析搜索结果为卡片列表（自动检测格式）
     *
     * @param searchResult 搜索返回的原始文本或结构化 JSON
     * @return 卡片列表
     */
    public static List<DisplayCard> parseToCards(String searchResult) {
        List<DisplayCard> cards = new ArrayList<>();

        if (StrUtil.isBlank(searchResult)) {
            return cards;
        }

        String trimmed = searchResult.trim();

        // 优先检测是否为结构化 JSON 数组格式（WebSearchTool 返回的 List<SearchResult>）
        if (trimmed.startsWith("[")) {
            try {
                JSONArray jsonArray = JSONUtil.parseArray(trimmed);
                return parseStructuredJson(jsonArray);
            } catch (Exception e) {
                log.debug("JSON 数组解析失败，降级为文本解析: {}", e.getMessage());
                // 降级为文本解析
            }
        }

        // 检测是否为单条 JSON 对象（部分工具可能返回单条）
        if (trimmed.startsWith("{")) {
            try {
                JSONObject jsonObj = JSONUtil.parseObj(trimmed);
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(jsonObj);
                return parseStructuredJson(jsonArray);
            } catch (Exception e) {
                log.debug("JSON 对象解析失败，降级为文本解析: {}", e.getMessage());
            }
        }

        // 降级：自由文本正则解析
        return parseFreeTextCards(searchResult);
    }

    /**
     * 解析结构化 JSON 数组为卡片列表
     * 适配 WebSearchTool 返回的 List<SearchResult> JSON 格式
     */
    private static List<DisplayCard> parseStructuredJson(JSONArray jsonArray) {
        List<DisplayCard> cards = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item == null) continue;

            String title = item.getStr("title", "");
            String content = item.getStr("content", "");
            String link = item.getStr("link", "");
            String publishDate = item.getStr("publish_date", "");
            String media = item.getStr("media", "");

            // 构建链接列表
            List<LinkItem> links = new ArrayList<>();
            if (StrUtil.isNotBlank(link)) {
                links.add(LinkItem.builder()
                        .url(link)
                        .title(StrUtil.isNotBlank(media) ? media : "来源链接")
                        .build());
            }

            // 构建描述（内容 + 日期）
            StringBuilder desc = new StringBuilder();
            if (StrUtil.isNotBlank(content)) {
                desc.append(content);
            }
            if (StrUtil.isNotBlank(publishDate)) {
                desc.append("\n发布日期：").append(publishDate);
            }

            // 构建标签
            List<String> tags = new ArrayList<>();
            if (StrUtil.isNotBlank(media)) {
                tags.add(media);
            }
            if (StrUtil.isNotBlank(publishDate)) {
                tags.add(publishDate);
            }

            DisplayCard card = DisplayCard.builder()
                    .title(title)
                    .description(desc.toString().trim())
                    .links(links)
                    .tags(tags)
                    .build();

            if (StrUtil.isNotBlank(card.getTitle())) {
                cards.add(card);
            }
        }

        log.info("结构化 JSON 解析完成，共 {} 条卡片", cards.size());
        return cards;
    }

    /**
     * 自由文本正则解析（兼容旧格式）
     */
    private static List<DisplayCard> parseFreeTextCards(String searchResult) {
        List<DisplayCard> cards = new ArrayList<>();

        // 方法1: 按 【数字】 分割
        List<String> sections = splitByNumberedBrackets(searchResult);

        if (sections.size() > 1) {
            // 有 【数字】格式，按段落解析
            for (String section : sections) {
                DisplayCard card = parseSection(section);
                if (card != null && StrUtil.isNotBlank(card.getTitle())) {
                    cards.add(card);
                }
            }
        } else {
            // 方法2: 按空行分割（每个地点一段）
            String[] paragraphs = searchResult.split("\\n\\n+");
            for (String paragraph : paragraphs) {
                DisplayCard card = parseSection(paragraph);
                if (card != null && StrUtil.isNotBlank(card.getTitle())) {
                    cards.add(card);
                }
            }
        }

        return cards;
    }

    /**
     * 按 【数字】 分割文本
     */
    private static List<String> splitByNumberedBrackets(String text) {
        List<String> sections = new ArrayList<>();

        // 使用正则表达式匹配 【数字】+ 内容
        Pattern pattern = Pattern.compile("【(\\d+)】([\\s\\S]*?)(?=【\\d+】|$)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String content = matcher.group(2).trim();
            if (StrUtil.isNotBlank(content)) {
                // 重新加上标题前的数字标记，方便解析
                sections.add("【" + matcher.group(1) + "】" + content);
            }
        }

        return sections;
    }

    /**
     * 解析单个段落为卡片
     */
    private static DisplayCard parseSection(String section) {
        if (StrUtil.isBlank(section)) {
            return null;
        }

        DisplayCard.DisplayCardBuilder builder = DisplayCard.builder();

        // 1. 提取标题
        String title = extractTitle(section);
        builder.title(title);

        // 2. 提取描述（标题后的内容）
        String description = extractDescription(section, title);
        builder.description(description);

        // 3. 提取链接
        List<LinkItem> links = extractLinks(section);
        builder.links(links);

        // 4. 提取图片
        List<ImageItem> images = extractImages(section);
        builder.images(images);

        // 5. 提取地址（如果有）
        String address = extractAddress(section);
        if (StrUtil.isNotBlank(address)) {
            builder.address(address);
        }

        return builder.build();
    }

    /**
     * 提取标题
     */
    private static String extractTitle(String text) {
        // 尝试1: 查找 【数字】后的标题
        Matcher bracketMatcher = ITEM_TITLE_PATTERN.matcher(text);
        if (bracketMatcher.find()) {
            return bracketMatcher.group(2).trim();
        }

        // 尝试2: 查找第一行的 "xxx：" 格式
        String[] lines = text.split("\\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            // 跳过常见的标题行，如 "搜索结果："、"xxx："等
            if (trimmedLine.matches("^(搜索结果|搜索|结果|Search Result|Results)：?$")) {
                continue;
            }
            Matcher colonMatcher = TITLE_COLON_PATTERN.matcher(trimmedLine);
            if (colonMatcher.find()) {
                return colonMatcher.group(1).trim();
            }
        }

        // 尝试3: 查找第一个非空行作为标题（跳过 "搜索结果：" 等行）
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (StrUtil.isBlank(trimmedLine)) {
                continue;
            }
            // 跳过常见的标题行
            if (trimmedLine.matches("^(搜索结果|搜索|结果|Search Result|Results)：?$")) {
                continue;
            }
            // 跳过以特殊符号开头的行
            if (trimmedLine.matches("^[【】\\[\\]]+$")) {
                continue;
            }
            // 如果这行不是太长且不包含URL，作为标题
            if (trimmedLine.length() < 100 && !trimmedLine.contains("http")) {
                return trimmedLine;
            }
        }

        return "";
    }

    /**
     * 提取描述（标题后的内容）
     */
    private static String extractDescription(String text, String title) {
        String desc = text;

        // 移除 【数字】标题标记
        desc = desc.replaceFirst("【\\d+】", "");

        // 移除标题
        if (StrUtil.isNotBlank(title) && !title.matches("【\\d+】.*")) {
            desc = desc.replace(Pattern.quote(title), "");
        }

        // 移除可能的前导符号
        desc = desc.replaceFirst("^[：：\\s]+", "");

        // 移除链接行
        desc = desc.replaceAll("链接：\\s*https?://[^\\s\\n\"'}]+", "");

        // 移除摘要行（避免重复）
        desc = desc.replaceAll("^摘要：\\s*", "");

        // 移除图片路径行
        desc = desc.replaceAll("(图片已保存到：|下载的图片：|图片：)\\s*.*?\\.(jpg|jpeg|png|gif|webp|bmp)", "");

        // 清理多余空行
        desc = desc.replaceAll("\\n{3,}", "\n\n");

        // 清理首尾空白
        desc = desc.trim();

        // 限制长度
        if (desc.length() > 500) {
            desc = desc.substring(0, 500) + "...";
        }

        return desc;
    }

    /**
     * 提取链接
     */
    private static List<LinkItem> extractLinks(String text) {
        List<LinkItem> links = new ArrayList<>();

        // 查找 "链接：xxx" 格式
        Matcher linkMatcher = LINK_URL_PATTERN.matcher(text);
        while (linkMatcher.find()) {
            String url = linkMatcher.group(1);
            // 尝试获取链接标题（前面的文字）
            String linkTitle = extractLinkTitleFromText(text, linkMatcher.start());
            links.add(LinkItem.builder()
                    .url(url)
                    .title(linkTitle)
                    .build());
        }

        // 如果没有找到，查找所有 URL
        if (links.isEmpty()) {
            Matcher urlMatcher = URL_PATTERN.matcher(text);
            while (urlMatcher.find()) {
                String url = urlMatcher.group();
                // 跳过图片链接
                if (!isImageUrl(url)) {
                    links.add(LinkItem.builder()
                            .url(url)
                            .title("参考链接")
                            .build());
                }
            }
        }

        return links;
    }

    /**
     * 从文本中提取链接标题
     */
    private static String extractLinkTitleFromText(String text, int linkStartPos) {
        // 获取链接前的内容
        int start = Math.max(0, linkStartPos - 100);
        String beforeText = text.substring(start, linkStartPos);

        // 移除 "链接：" 前缀
        beforeText = beforeText.replaceFirst("链接：\\s*$", "");

        // 获取最后一行
        String[] lines = beforeText.split("\\n");
        if (lines.length > 0) {
            String lastLine = lines[lines.length - 1].trim();
            // 跳过太长的行和包含"摘要"的行
            if (StrUtil.isNotBlank(lastLine) && lastLine.length() < 60
                    && !lastLine.contains("摘要") && !lastLine.contains("http")) {
                return lastLine;
            }
        }

        return "详情链接";
    }

    /**
     * 提取图片（公开方法，供外部调用）
     */
    public static List<ImageItem> extractImages(String text) {
        List<ImageItem> images = new ArrayList<>();

        // 查找 "图片已保存到：" 或 "下载的图片：" 格式
        Matcher imageMatcher = IMAGE_PATH_PATTERN.matcher(text);
        while (imageMatcher.find()) {
            String imagePath = imageMatcher.group(2);
            images.add(ImageItem.builder()
                    .url(imagePath)
                    .description("相关图片")
                    .build());
        }

        // 查找图片 URL
        Matcher urlMatcher = URL_PATTERN.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            if (isImageUrl(url)) {
                images.add(ImageItem.builder()
                        .url(url)
                        .description("相关图片")
                        .build());
            }
        }

        return images;
    }

    /**
     * 提取地址信息
     */
    private static String extractAddress(String text) {
        // 查找 "地址：" 格式
        Pattern addressPattern = Pattern.compile("地址[：:]\\s*([^\\n]+?)(?=\\n|$)");
        Matcher matcher = addressPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // 查找 "位于" 格式
        Pattern locatedPattern = Pattern.compile("位于\\s*([^\\n，。]+?)(?=[，。\\n]|$)");
        Matcher locatedMatcher = locatedPattern.matcher(text);
        if (locatedMatcher.find()) {
            return "位于 " + locatedMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * 判断是否为图片 URL
     */
    private static boolean isImageUrl(String url) {
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg") ||
               lowerUrl.contains(".png") || lowerUrl.contains(".gif") ||
               lowerUrl.contains(".webp") || lowerUrl.contains(".bmp");
    }

    /**
     * 显示卡片 - 前端可直接展示的数据结构
     */
    @Data
    @Builder
    public static class DisplayCard {
        /** 标题 */
        private String title;

        /** 描述 */
        private String description;

        /** 地址 */
        private String address;

        /** 图片列表 */
        private List<ImageItem> images;

        /** 链接列表 */
        private List<LinkItem> links;

        /** 标签/分类 */
        private List<String> tags;

        /** 原始数据（备用） */
        private String rawData;
    }

    @Data
    @Builder
    public static class ImageItem {
        private String url;
        private String description;
        private String thumbnail;
    }

    @Data
    @Builder
    public static class LinkItem {
        private String url;
        private String title;
        private String description;
    }
}
