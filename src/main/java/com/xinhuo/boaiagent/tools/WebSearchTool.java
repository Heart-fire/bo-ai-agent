package com.xinhuo.boaiagent.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.xinhuo.boaiagent.constant.BigModelConstant;
import com.xinhuo.boaiagent.tools.dto.BigModelWebSearchRequest;
import com.xinhuo.boaiagent.tools.dto.BigModelWebSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 网页搜索工具类（基于智谱 AI Web Search API）
 * 英文识别度高
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
     * 网页搜索（高级）
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
                    .search_engine("search_std")
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
            return formatSearchResult(response);

        } catch (Exception e) {
            log.error("网页搜索失败", e);
            return "网页搜索失败: " + e.getMessage();
        }
    }

    /**
     * 格式化搜索结果（简洁格式，用于 LLM 总结）
     */
    private String formatSearchResult(BigModelWebSearchResponse response) {
        if (response == null || response.getSearch_result() == null || response.getSearch_result().isEmpty()) {
            return "未找到相关结果";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("搜索结果：\n\n");

        for (int i = 0; i < response.getSearch_result().size(); i++) {
            BigModelWebSearchResponse.SearchResult result = response.getSearch_result().get(i);
            sb.append("【").append(i + 1).append("】").append(result.getTitle()).append("\n");
            sb.append("链接：").append(result.getLink()).append("\n");
            if (result.getContent() != null && !result.getContent().isEmpty()) {
                // 限制摘要长度，避免太长
                String content = result.getContent();
                if (content.length() > 200) {
                    content = content.substring(0, 200) + "...";
                }
                sb.append("摘要：").append(content).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
