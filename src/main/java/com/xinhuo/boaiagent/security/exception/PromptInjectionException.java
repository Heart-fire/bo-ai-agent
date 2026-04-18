package com.xinhuo.boaiagent.security.exception;

import com.xinhuo.boaiagent.security.model.ThreatLevel;
import com.xinhuo.boaiagent.security.model.ThreatReport;
import lombok.Getter;

import java.util.List;

/**
 * Prompt 注入检测异常
 */
@Getter
public class PromptInjectionException extends RuntimeException {

    private final ThreatLevel threatLevel;
    private final List<ThreatReport> reports;

    public PromptInjectionException(String message, ThreatLevel threatLevel, List<ThreatReport> reports) {
        super(message);
        this.threatLevel = threatLevel;
        this.reports = reports;
    }
}
