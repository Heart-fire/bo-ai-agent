package com.xinhuo.boaiagent.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.xinhuo.boaiagent.constant.BigModelConstant;
import com.xinhuo.boaiagent.tools.dto.BigModelWebSearchRequest;
import com.xinhuo.boaiagent.tools.dto.BigModelWebSearchResponse;
import com.xinhuo.boaiagent.tools.dto.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 网页搜索工具类
 */
@Slf4j
@Component
public class WebSearchTool {

    @Value("${bigmodel.api-key}")
    private String apiKey;

    /**
     * 网页搜索
     * @param searchQuery 搜索关键词
     * @return 搜索结果 JSON 字符串
     */
    @Tool(description = "Web search, searching for information on the Internet")
    public String webSearch(@ToolParam(description = "Search keywords") String searchQuery) {
        return webSearchAdvanced(searchQuery, 5, null, null);
    }


    /**
     * 网页搜索（集成搜狗搜索）
     * @param searchQuery 搜索关键词
     * @param count 返回结果数量
     * @param domainFilter 域名过滤
     * @param recencyFilter 时间过滤（noLimit/day/week/month/year）
     * @return 搜索结果 JSON 字符串
     */
    @Tool(description = "Web search (advanced), supports custom result count and filtering conditions")
    public String webSearchAdvanced(
            @ToolParam(description = "Search keywords") String searchQuery,
            @ToolParam(description = "Number of results to return, default is 5") Integer count,
            @ToolParam(description = "Domain filter, e.g. example.com") String domainFilter,
            @ToolParam(description = "Recency filter: noLimit/day/week/month/year") String recencyFilter
    ) {
        try {
            BigModelWebSearchRequest request = BigModelWebSearchRequest.builder()
                    .search_query(searchQuery)
                    .search_engine("search_pro_sogou") // 搜狗搜索
                    .search_intent(false)
                    .count(count != null ? count : 5)
                    .search_domain_filter(domainFilter)
                    .search_recency_filter(recencyFilter != null ? recencyFilter : "noLimit")
                    .content_size("medium")
                    .build();

            String responseBody = HttpRequest.post(BigModelConstant.WEB_SEARCH_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(JSONUtil.toJsonStr(request))
                    .execute()
                    .body();

            log.info("智谱搜索原始响应: {}", responseBody);

            BigModelWebSearchResponse response = JSONUtil.toBean(responseBody, BigModelWebSearchResponse.class);

            // 格式化输出搜索结果
            return JSONUtil.toJsonStr(formatSearchResult(response, count));

        } catch (Exception e) {
            log.error("网页搜索失败", e);
            return "网页搜索失败: " + e.getMessage();
        }
    }


    /**
     * 格式化搜索结果（简洁格式，用于 LLM 总结）
     */
    private List<SearchResult> formatSearchResult(BigModelWebSearchResponse response, Integer count) {
        if (response == null || response.getSearch_result() == null) {
            return Collections.emptyList();
        }

        List<SearchResult> results = response.getSearch_result();

        // 排序
        results.sort((o1, o2) -> {
            String d1 = o1.getPublish_date();
            String d2 = o2.getPublish_date();
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        int limit = Math.min(count != null ? count : 5, results.size());

        List<SearchResult> list = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            SearchResult r = results.get(i);

            SearchResult dto = new SearchResult();
            dto.setTitle(cleanText(r.getTitle()));
            dto.setLink(cleanText(r.getLink()));
            dto.setContent(cleanText(r.getContent()));
            dto.setPublish_date(r.getPublish_date());

            list.add(dto);
        }

        return list;
    }

    /**
     * 清理文本，去除换行、多余空白、常见gov网站噪音
     */
    private String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 1. 去换行
        text = text.replaceAll("[\\r\\n]+", " ");

        // 2. 去多余空白
        text = text.replaceAll("\\s+", " ").trim();

        // 3. 过滤政务网站常见噪音
        text = text.replaceAll("我在听.*?再说一遍吧", "");
        text = text.replaceAll("切换区域", "");
        text = text.replaceAll("切换简洁版", "");

        return text.trim();
    }
}
