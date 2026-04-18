package com.xinhuo.boaiagent.security.guard;

import com.xinhuo.boaiagent.security.model.ThreatLevel;
import com.xinhuo.boaiagent.security.model.ThreatReport;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 安全检查结果
 */
@Data
@Builder
public class InputGuardResult {

    /**
     * 是否应阻断请求
     */
    private boolean blocked;

    /**
     * 威胁等级
     */
    private ThreatLevel threatLevel;

    /**
     * 返回给用户的提示消息
     */
    private String message;

    /**
     * 各层检测报告详情
     */
    private List<ThreatReport> reports;

    public static InputGuardResult safe() {
        return InputGuardResult.builder()
                .blocked(false)
                .threatLevel(ThreatLevel.SAFE)
                .message("")
                .reports(Collections.emptyList())
                .build();
    }

    public static InputGuardResult blocked(ThreatLevel level, String message, List<ThreatReport> reports) {
        return InputGuardResult.builder()
                .blocked(true)
                .threatLevel(level)
                .message(message)
                .reports(reports)
                .build();
    }
}
