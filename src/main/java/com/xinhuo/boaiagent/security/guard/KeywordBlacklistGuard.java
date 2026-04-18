package com.xinhuo.boaiagent.security.guard;

import com.xinhuo.boaiagent.security.model.ThreatLevel;
import com.xinhuo.boaiagent.security.model.ThreatReport;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Layer1: 关键词黑名单过滤
 * 基于正则表达式对用户输入做初步筛查，拦截明显的 prompt injection 攻击
 */
@Slf4j
public class KeywordBlacklistGuard {

    private static final String GUARD_NAME = "KeywordBlacklistGuard";

    private final List<Pattern> blacklistedPatterns;
    private final boolean enabled;

    public KeywordBlacklistGuard(List<String> patternStrings, boolean enabled) {
        this.enabled = enabled;
        this.blacklistedPatterns = patternStrings.stream()
                .map(Pattern::compile)
                .toList();
    }

    public ThreatReport check(String input) {
        if (!enabled || input == null || input.isBlank()) {
            return ThreatReport.safe(GUARD_NAME);
        }

        String normalizedInput = normalize(input);
        for (Pattern pattern : blacklistedPatterns) {
            if (pattern.matcher(normalizedInput).find()) {
                log.warn("关键字黑名单触发: pattern={}, input={}",
                        pattern.pattern(), truncate(input, 100));
                return ThreatReport.builder()
                        .threatLevel(ThreatLevel.MALICIOUS)
                        .guardName(GUARD_NAME)
                        .reason("输入匹配到黑名单模式: " + pattern.pattern())
                        .originalInput(input)
                        .matchedPattern(pattern.pattern())
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
            }
        }
        return ThreatReport.safe(GUARD_NAME);
    }

    /**
     * 标准化输入：小写、合并空白、去除控制字符
     */
    private String normalize(String input) {
        return input.toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("[\\u0000-\\u001F]", "")
                .trim();
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
