package com.xinhuo.boaiagent.security.guard;

import com.xinhuo.boaiagent.security.model.ThreatLevel;
import com.xinhuo.boaiagent.security.model.ThreatReport;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全防护门面
 * 串联三层安全检查：关键词过滤 → 意图识别 → 多模型交叉验证
 * 任何一层返回 MALICIOUS 立即阻断，不再执行后续层
 */
@Slf4j
public class InputGuard {

    private final KeywordBlacklistGuard keywordGuard;
    private final IntentRecognitionGuard intentGuard;
    private final CrossValidationGuard crossValidationGuard;
    private final boolean globalEnabled;

    public InputGuard(KeywordBlacklistGuard keywordGuard,
                      IntentRecognitionGuard intentGuard,
                      CrossValidationGuard crossValidationGuard,
                      boolean globalEnabled) {
        this.keywordGuard = keywordGuard;
        this.intentGuard = intentGuard;
        this.crossValidationGuard = crossValidationGuard;
        this.globalEnabled = globalEnabled;
    }

    /**
     * 主入口：对用户输入执行安全检查
     *
     * @param input 用户原始输入
     * @return 检查结果，blocked=true 表示应阻断请求
     */
    public InputGuardResult check(String input) {
        if (!globalEnabled) {
            log.debug("禁用安全防护，允许输入");
            return InputGuardResult.safe();
        }

        List<ThreatReport> allReports = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Layer1: 关键词黑名单（最快，~0ms）
        ThreatReport keywordReport = keywordGuard.check(input);
        allReports.add(keywordReport);
        if (keywordReport.getThreatLevel() == ThreatLevel.MALICIOUS) {
            log.warn("在  {} 毫秒内被关键字黑名单阻止", System.currentTimeMillis() - startTime);
            return buildBlockedResult(keywordReport, allReports, startTime);
        }

        // Layer2: AI 意图识别（中等成本，~1-3s）
        ThreatReport intentReport = intentGuard.check(input);
        allReports.add(intentReport);
        if (intentReport.getThreatLevel() == ThreatLevel.MALICIOUS) {
            log.warn("在 {} 毫秒内被意图识别阻止", System.currentTimeMillis() - startTime);
            return buildBlockedResult(intentReport, allReports, startTime);
        }

        // Layer3: 多模型交叉验证（高成本，仅在 Layer2 标记为 SUSPICIOUS 时触发）
        if (intentReport.getThreatLevel() == ThreatLevel.SUSPICIOUS) {
            ThreatReport crossReport = crossValidationGuard.check(input);
            allReports.add(crossReport);
            if (crossReport.getThreatLevel() == ThreatLevel.MALICIOUS
                    || crossReport.getThreatLevel() == ThreatLevel.SUSPICIOUS) {
                log.warn("在 {} 毫秒内被交叉验证阻止", System.currentTimeMillis() - startTime);
                return buildBlockedResult(crossReport, allReports, startTime);
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("输入 {} 的安全检查在 {} 毫秒内通过 ", elapsed, truncate(input, 80));
        return InputGuardResult.safe();
    }

    private InputGuardResult buildBlockedResult(ThreatReport trigger, List<ThreatReport> reports, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        String userMessage = "亲，您的输入有些异常，触发了安全保护机制呢～作为您的政策答疑助手，希望您能用正常的语言提问政策相关的问题，我会很乐意帮助您。感谢您的理解与配合哦！";
        log.warn("安全拦截，耗时{}ms：守卫={}，原因={}", elapsed, trigger.getGuardName(), trigger.getReason());
        return InputGuardResult.blocked(trigger.getThreatLevel(), userMessage, reports);
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
