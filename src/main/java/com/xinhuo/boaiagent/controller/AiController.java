package com.xinhuo.boaiagent.controller;

import com.xinhuo.boaiagent.agent.ResearchAgent;
import com.xinhuo.boaiagent.app.PolicyApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AI 接口控制器
 * - /ai/policy_app/** ：北京政策问答（纯聊天 + RAG）
 * - /ai/research/**   ：公开信息采集研究员（自主 Agent）
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private PolicyApp policyApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;


    /**
     * 政策问答 - 基础多轮对话（同步）
     *
     * @param message 用户输入
     * @param chatId  会话 ID（用于多轮记忆隔离）
     */
    @GetMapping("/policy_app/chat")
    public String doChatWithPolicyApp(String message, String chatId) {
        return policyApp.doChat(message, chatId);
    }

    /**
     * 政策问答 - 流式输出（SSE）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
    @GetMapping(value = "/policy_app/chat/sse")
    public SseEmitter doChatWithPolicyAppBySse(String message, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(180_000L); // 3 分钟超时
        policyApp.doChatByStream(message, chatId)
                .subscribe(
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete
                );
        return sseEmitter;
    }

    /**
     * 政策问答 - RAG 增强对话（查询重写 + 知识库召回）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
    @GetMapping("/policy_app/chat/rag")
    public String doChatWithPolicyAppRag(String message, String chatId) {
        return policyApp.doChatWithRag(message, chatId);
    }

    /**
     * 政策问答 - 结构化输出（政策摘要报告）
     *
     * @param message 用户输入
     * @param chatId  会话 ID
     */
    @GetMapping("/policy_app/chat/report")
    public Object doChatWithPolicyAppReport(String message, String chatId) {
        return policyApp.doChatWithReport(message, chatId);
    }



    /**
     * 研究员 Agent - 流式执行（SSE）
     * Agent 自主规划：搜索 → 抓取 → 整理 → 生成 PDF 报告
     */
    @GetMapping("/research/chat")
    public SseEmitter doChatWithResearchAgent(String message) {
        ResearchAgent researchAgent = new ResearchAgent(allTools, dashscopeChatModel);
        return researchAgent.runStream(message);
    }

    /**
     * 流式执行（SSE）
     * 使用 ResearchAgent 作为底层实现
     *
     * @param message 用户问题
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        ResearchAgent researchAgent = new ResearchAgent(allTools, dashscopeChatModel);
        return researchAgent.runStream(message);
    }


    /**
     * 文件下载端点 - 提供 tmp/pdf/ 目录下的文件下载
     *
     * @param filename 文件名
     */
    @GetMapping("/files/{*filename}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), "tmp", "pdf", filename);
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"download.pdf\"; filename*=UTF-8''" + encodedName)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
