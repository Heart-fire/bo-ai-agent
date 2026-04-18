package com.xinhuo.boaiagent.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 威胁检测报告
 */
@Data
@Builder
public class ThreatReport {

    /**
     * 威胁等级
     */
    private ThreatLevel threatLevel;

    /**
     * 触发检测的 Guard 名称
     */
    private String guardName;

    /**
     * 判定原因说明
     */
    private String reason;

    /**
     * 原始用户输入
     */
    private String originalInput;

    /**
     * 匹配到的模式（关键词检测用）
     */
    private String matchedPattern;

    /**
     * 置信度 0.0-1.0（AI 检测用）
     */
    private double confidence;

    /**
     * 检测时间
     */
    private LocalDateTime timestamp;

    public static ThreatReport safe(String guardName) {
        return ThreatReport.builder()
                .threatLevel(ThreatLevel.SAFE)
                .guardName(guardName)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
