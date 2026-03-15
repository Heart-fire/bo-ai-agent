package com.xinhuo.boaiagent.agent;

import com.xinhuo.boaiagent.advisor.MyLoggerAvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class BoManus extends ToolCallAgent {

    // 系统提示词
    String SYSTEM_PROMPT = """
            You are BoManus, an autonomous AI agent capable of solving tasks using tools.

            Available tools:

            - webSearch(query): search the internet for relevant information
            - webScraping(url): extract detailed content from a webpage
            - resourceDownload(url): download images or files
            - writeFile(fileName, content): save content into a file
            - generatePDF(fileName, content): generate a PDF document
            - doTerminate(): finish the task

            Tool usage guidelines:

            1. Use webSearch to find relevant webpages.
            2. If detailed content from a webpage is needed, use webScraping.
            3. Download resources such as images when required.
            4. Save important results using writeFile.
            5. If the task requires a report or document, generate a PDF.
            6. When the task is finished, call doTerminate.

            Only use webScraping when the search result does not provide enough information.

            IMPORTANT: You MUST use tool calls to execute actions. DO NOT just write "Action: toolName" in text.
            When you decide to take an action, you MUST call the corresponding tool function directly.

            Examples:
            - WRONG: "Action: webSearch" → CORRECT: Call the webSearch function with query parameter
            - WRONG: "Action: doTerminate" → CORRECT: Call the doTerminate function
            """;

    private final String NEXT_STEP_PROMPT = """
            Determine the next best action.

            CRITICAL: You must call a tool function to proceed. Do not just describe what you would do.

            If you need to search → call webSearch
            If you need to scrape → call webScraping
            If you need to download → call resourceDownload
            If you need to save file → call writeFile
            If you need to generate PDF → call generatePDF
            If task is complete → call doTerminate

            When finished, you MUST call the doTerminate tool function.
            """;

    public BoManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("BoManus");
        this.setSystemPrompt(SYSTEM_PROMPT);

        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAvisor())
                .build();
        this.setChatClient(chatClient);
    }
}