package com.xinhuo.boaiagent.security.model;

import lombok.Getter;

/**
 * 威胁等级枚举
 */
@Getter
public enum ThreatLevel {

    SAFE(0, "安全"),
    SUSPICIOUS(1, "可疑"),
    MALICIOUS(2, "恶意");

    private final int level;
    private final String label;

    ThreatLevel(int level, String label) {
        this.level = level;
        this.label = label;
    }
}
