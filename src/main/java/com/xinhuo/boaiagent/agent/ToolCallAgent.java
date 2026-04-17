package com.xinhuo.boaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.xinhuo.boaiagent.agent.model.AgentState;
import com.xinhuo.boaiagent.agent.model.SearchResultParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存工具调用信息的响应结果（要调用哪些工具）
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    // 保存最后一次的思考内容，用于返回给前端
    private String lastThoughtContent = "";

    // 保存已使用的工具列表
    private final List<String> usedTools = new ArrayList<>();

    // 保存所有步骤的原始结果
    private final List<String> rawResults = new ArrayList<>();

    // 保存搜索类工具的卡片结果（用于 generateSummary）
    private final List<SearchResultParser.DisplayCard> searchCards = new ArrayList<>();

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        // 1、校验提示词，拼接用户提示词
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        // 2、调用 AI 大模型，获取工具调用结果
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于等下 Act
            this.toolCallChatResponse = chatResponse;
            // 3、解析工具调用结果，获取要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            // 输出提示信息
            String result = assistantMessage.getText();
            // 保存思考内容
            this.lastThoughtContent = result;
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // 如果不需要调用工具，返回 false
            if (toolCallList.isEmpty()) {
                // 只有不调用工具时，才需要手动记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题：" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            this.lastThoughtContent = "处理时遇到了错误：" + e.getMessage();
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果（JSON 格式）
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return createErrorResponse("没有工具需要调用");
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            // 任务结束，更改状态
            setState(AgentState.FINISHED);
        }

        // 构建返回结果
        List<CardResult> cardResults = new ArrayList<>();
        String toolName = "";
        for (ToolResponseMessage.ToolResponse response : toolResponseMessage.getResponses()) {
            toolName = response.name();
            String toolData = response.responseData();

            // 记录使用的工具
            if (!usedTools.contains(toolName)) {
                usedTools.add(toolName);
            }

            // 保存原始结果
            rawResults.add(toolData);

            // 根据工具类型解析结果
            if ("webSearch".equals(toolName) || "webSearchAdvanced".equals(toolName) || "webScraping".equals(toolName)) {
                // 直接用正则解析 搜索结果为卡片，无需额外 LLM 调用
                List<SearchResultParser.DisplayCard> cards = SearchResultParser.parseToCards(toolData);
                for (SearchResultParser.DisplayCard card : cards) {
                    cardResults.add(CardResult.builder()
                            .toolName(toolName)
                            .card(card)
                            .build());
                    searchCards.add(card);
                }
            } else if ("resourceDownload".equals(toolName)) {
                // 下载工具：返回图片/文件信息
                cardResults.add(parseDownloadResult(toolData, toolName));
            } else {
                // 其他工具：返回原始文本
                cardResults.add(CardResult.builder()
                        .toolName(toolName)
                        .rawText(toolData)
                        .build());
            }

            log.info("工具 {} 返回的结果：{}", toolName,
                    toolData.length() > 200 ? toolData.substring(0, 200) + "..." : toolData);
        }
        Map<String, Object> action = new HashMap<>();
        action.put("type", "action");
        action.put("tool", toolName);
        action.put("data", cardResults);

        return JSONUtil.toJsonStr(action);
    }

    /**
     * 解析下载结果
     */
    private CardResult parseDownloadResult(String toolData, String toolName) {
        SearchResultParser.DisplayCard card = SearchResultParser.DisplayCard.builder()
                .title("下载完成")
                .description(toolData)
                .images(SearchResultParser.extractImages(toolData))
                .build();

        return CardResult.builder()
                .toolName(toolName)
                .card(card)
                .build();
    }

    /**
     * 创建错误响应
     */
    private String createErrorResponse(String message) {
        return JSONUtil.toJsonStr(java.util.Map.of(
                "type", "error",
                "message", message
        ));
    }

    /**
     * 获取思考内容
     *
     * @return AI 的思考内容
     */
    @Override
    protected String getThoughtContent() {
        return this.lastThoughtContent;
    }

    /**
     * 生成智能总结（使用搜索类工具的卡片结果）
     *
     * @param results 所有步骤的结果
     * @return JSON 格式的总结（包含卡片列表）
     */
    @Override
    protected String generateSummary(List<String> results) {
        try {
            log.info("开始生成总结，搜索卡片数: {}", searchCards.size());

            // 去重（根据标题）
            List<SearchResultParser.DisplayCard> uniqueCards = searchCards.stream()
                    .filter(card -> StrUtil.isNotBlank(card.getTitle()))
                    .collect(java.util.stream.Collectors.toMap(
                            SearchResultParser.DisplayCard::getTitle,
                            card -> card,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .toList();

            log.info("去重后卡片数: {}", uniqueCards.size());

            // 构建总结响应
            java.util.Map<String, Object> summary = new java.util.LinkedHashMap<>();
            summary.put("type", "summary");
            summary.put("totalCards", uniqueCards.size());
            summary.put("toolsUsed", usedTools);
            summary.put("conclusion", generateConclusion(uniqueCards));
            summary.put("cards", uniqueCards);

            String jsonResult = JSONUtil.toJsonStr(summary);
            log.info("总结 JSON 生成完成，长度: {} 字符", jsonResult.length());

            return jsonResult;

        } catch (Exception e) {
            log.error("生成总结失败", e);
            java.util.Map<String, Object> simpleSummary = new java.util.LinkedHashMap<>();
            simpleSummary.put("type", "summary");
            simpleSummary.put("conclusion", "任务已完成，共执行 " + results.size() + " 个步骤");
            simpleSummary.put("cards", new ArrayList<>());
            return JSONUtil.toJsonStr(simpleSummary);
        }
    }

    /**
     * 从卡片列表生成总结结论
     */
    private String generateConclusion(List<SearchResultParser.DisplayCard> cards) {
        if (cards.isEmpty()) {
            return "任务已完成";
        }

        if (cards.size() <= 3) {
            // 少量卡片，列出标题
            String titles = cards.stream()
                    .map(SearchResultParser.DisplayCard::getTitle)
                    .collect(Collectors.joining("、"));
            return "已为您找到 " + cards.size() + " 个结果：" + titles;
        } else {
            // 多个卡片，概括
            return "已为您找到 " + cards.size() + " 个相关结果";
        }
    }

    /**
     * 流式输出总结文本：使用 ChatClient 流式调用 LLM，逐 token 推送 SSE 事件
     */
    // TODO 优化
    @Override
    protected void streamSummary(org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                                  List<String> results) {
        try {
            // 去重（与 generateSummary 保持一致，按标题去重）
            List<SearchResultParser.DisplayCard> uniqueCards = searchCards.stream()
                    .filter(card -> StrUtil.isNotBlank(card.getTitle()))
                    .collect(java.util.stream.Collectors.toMap(
                            SearchResultParser.DisplayCard::getTitle,
                            card -> card,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .toList();

            // 构建上下文：从去重后的卡片中提取关键信息
            StringBuilder context = new StringBuilder();
            for (SearchResultParser.DisplayCard card : uniqueCards) {
                if (StrUtil.isNotBlank(card.getTitle())) {
                    context.append("【").append(card.getTitle()).append("】");
                }
                if (StrUtil.isNotBlank(card.getDescription())) {
                    context.append(card.getDescription());
                }
                context.append("\n");
            }

            if (context.isEmpty()) {
                return;
            }

            String prompt = "请根据以下搜索到的研究信息，用简洁流畅的语言为用户做一个总结，" +
                    "直接输出总结文字，使用对话语气，不要使用 JSON 或 markdown 格式，不要加标题：\n\n" + context;

            // 使用 ChatClient 流式输出
            getChatClient().prompt()
                    .user(prompt)
                    .stream()
                    .content()
                    .toStream()
                    .forEach(chunk -> {
                        if (chunk == null || chunk.isEmpty()) return;
                        Map<String, Object> summaryChunk = Map.of(
                                "type", "summary_stream",
                                "content", chunk
                        );
                        sendSse(emitter, cn.hutool.json.JSONUtil.toJsonStr(summaryChunk));
                    });

            // 发送流式总结完成标记
            sendSse(emitter, "{\"type\":\"summary_done\"}");

        } catch (com.xinhuo.boaiagent.agent.model.SseClosedException e) {
            throw e;
        } catch (Exception e) {
            log.error("流式总结生成失败", e);
        }
    }

    /**
     * 卡片结果 - 返回给前端的数据结构
     */
    @Data
    @lombok.Builder
    private static class CardResult {
        private String toolName;
        private SearchResultParser.DisplayCard card;
        private String rawText;
    }
}
