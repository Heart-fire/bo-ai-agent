package com.xinhuo.boaiagent.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容摘要
     */
    private String content;

    /**
     * 链接
     */
    private String link;

    /**
     * 媒体
     */
    private String media;

    /**
     * 图标
     */
    private String icon;

    /**
     * 来源
     */
    private String refer;

    /**
     * 发布日期
     */
    private String publish_date;
}