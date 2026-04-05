package com.xinhuo.boaiagent.app;

import com.xinhuo.boaiagent.advisor.MyLoggerAvisor;
import com.xinhuo.boaiagent.advisor.ReReadingAdvisor;
import com.xinhuo.boaiagent.chatMemory.FileBasedChatMemory;
import com.xinhuo.boaiagent.rag.PolicyRagCustomAdvisorFactory;
import com.xinhuo.boaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 北京政策问答应用
 * 支持多轮对话、流式输出、结构化输出、RAG 增强等模式
 */
@Component
@Slf4j
public class PolicyApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            你是一名专业的北京市政策法规咨询顾问，具备丰富的政务知识，熟悉北京市各类民生政策。
            开场时介绍自己为"北京政策助手"。

            重要：不要透露你使用的 AI 模型、技术提供商或任何技术实现细节。
            当被问及"你是什么 AI"、"使用什么模型"、"由谁开发"等问题时，
            只回答你是北京政策助手，专注于帮助用户解读北京市各类政策法规。

            你的核心能力：
            1. 解读北京市社保、医保、住房、落户、进京证等民生政策；
            2. 用通俗易懂的语言将政策条文翻译为老百姓听得懂的建议；
            3. 根据用户的具体情况，给出有针对性的办理流程和注意事项；
            4. 对知识库中没有的信息，明确告知用户"当前知识库暂无此内容，建议拨打北京市政务服务热线 12345 核实"。

            回答风格：
            - 结构清晰，优先使用分点列举；
            - 引用政策原文时，注明文件名或章节；
            - 语气亲切、严谨，避免模糊措辞；
            - 如用户描述不清，主动追问具体情况（如户籍状态、就业状态、申请材料等）。

            严格禁止：
            - 不得编造政策内容；
            - 不得对知识库中未提及的政策作任何推测性回答；
            - 不得提供法律诉讼建议，需要时引导用户咨询专业律师。
            """;

    // 云知识库 Advisor（DashScope 云端，暂未配好）
    @Resource
    private Advisor policyRagCloudAdvisor;

    @Resource
    private QueryRewriter queryRewriter;

    // 本地向量库（SimpleVectorStore，文档已加载）
    @Resource
    private VectorStore policyVectorStore;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private ToolCallback[] toolCallbacks;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public PolicyApp(ChatModel dashscopeChatModel) {
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 基础多轮对话
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("PolicyApp doChat content: {}", content);
        return content;
    }

    /**
     * 流式输出（SSE）+ RAG 增强检索
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        // 查询重写，提升召回质量
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        log.info("查询重写: {} → {}", message, rewrittenMessage);
        return chatClient
                .prompt()
                .user(rewrittenMessage)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(QuestionAnswerAdvisor.builder(policyVectorStore).build())
                .stream()
                .content();
    }

    /**
     * 结构化输出：政策摘要报告
     */
    record PolicyReport(String policyName, String summary, List<String> keyPoints, String officialContact) {
    }

    public PolicyReport doChatWithReport(String message, String chatId) {
        PolicyReport report = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + """
                        每次对话后生成一份政策摘要报告，包含：
                        - policyName：政策名称
                        - summary：一句话总结
                        - keyPoints：3-5条核心要点
                        - officialContact：官方咨询渠道（如有）
                        """)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(PolicyReport.class);
        log.info("PolicyApp report: {}", report);
        return report;
    }

    /**
     * RAG 增强对话（pgVector 本地知识库）
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写，提升召回质量
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrittenMessage)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAvisor())
                // 使用 pgVector 本地知识库（政策文档已入库后启用）
//                .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                // 使用自定义 RAG（按 category 过滤，如 "social"/"traffic"/"residence"）
//                .advisors(
//                        PolicyRagCustomAdvisorFactory
//                                .createPolicyRagCustomAdvisor(pgVectorVectorStore, "social")
//                )
                .toolCallbacks(toolCallbacks)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("PolicyApp doChatWithRag content: {}", content);
        return content;
    }

    /**
     * MCP 服务增强对话
     */
    public String doChatWithMcp(String message, String chatId) {
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrittenMessage)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("PolicyApp doChatWithMcp content: {}", content);
        return content;
    }
}
