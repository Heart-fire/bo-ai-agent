package com.xinhuo.boaiagent.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智谱 AI 网页搜索请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BigModelWebSearchRequest {

    /**
     * 搜索关键词
     */
    private String search_query;

    /**
     * 搜索引擎，默认 search_std
     */
    private String search_engine;

    /**
     * 搜索意图分析，默认 false
     */
    private Boolean search_intent;

    /**
     * 返回结果数量，默认 10
     */
    private Integer count;

    /**
     * 搜索域名过滤
     */
    private String search_domain_filter;

    /**
     * 搜索时间过滤，noLimit/day/week/month/year
     */
    private String search_recency_filter;

    /**
     * 内容大小，small/medium/large
     */
    private String content_size;

    /**
     * 请求 ID
     */
    private String request_id;

    /**
     * 用户 ID
     */
    private String user_id;
}
