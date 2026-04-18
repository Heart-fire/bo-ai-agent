package com.xinhuo.boaiagent.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智谱 AI 网页搜索响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BigModelWebSearchResponse {

    /**
     * 响应 ID
     */
    private String id;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 请求 ID
     */
    private String request_id;

    /**
     * 搜索意图列表
     */
    private List<SearchIntent> search_intent;

    /**
     * 搜索结果列表
     */
    private List<SearchResult> search_result;

    /**
     * 搜索意图
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchIntent {
        /**
         * 查询内容
         */
        private String query;

        /**
         * 意图类型
         */
        private String intent;

        /**
         * 关键词
         */
        private String keywords;
    }


}
