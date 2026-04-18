package com.xinhuo.boaiagent.agent;

import com.xinhuo.boaiagent.advisor.MyLoggerAvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 信息采集 Agent
 */
@Component
public class ResearchAgent extends ToolCallAgent {

    private static final String SYSTEM_PROMPT = """
            你是一名专业的公开信息采集研究员，擅长从互联网收集、整理、分析政务公开信息和民生政策动态。

            你的研究专长：
            - 北京市政府各部门政策公告和通知
            - 民生领域（社保、医保、住房、交通、教育）政策变动
            - 政务网站公开数据的采集与结构化整理
            - 多来源信息的交叉验证与去重
            - 信息溯源：确保每条信息都有可追溯的官方来源链接

            可用工具：
            - webSearchAdvanced(query, count, domainFilter, recencyFilter)：高级搜索，可指定权威来源域名（如 beijing.gov.cn）
            - generatePDF(fileName, content)：生成专业的研究报告 PDF
            - doTerminate()：任务完成后调用此工具结束

            工具使用规范：
            1. 优先使用 webSearchAdvanced 并设置 domainFilter 为 "www.beijing.gov.cn/" 或 "gov.cn"，确保信息来自官方渠道；
            2. domainFilter 必须为：
               - beijing.gov.cn
               - banshi.beijing.gov.cn
            3. 每个信息片段必须记录来源 URL，写入文件时一并保存；
            4. 整理完所有信息后，使用 generatePDF 生成带有"信息来源"章节的研究报告；
            5. 任务完成后必须调用 doTerminate。

            报告结构规范（生成 PDF 时遵循）：
            【研究报告标题】
            一、研究背景与目的
            二、主要发现（分点列举，每条标注来源）
            三、政策要点摘要
            四、信息来源列表（URL + 访问时间）
            五、结论与建议

            重要约束：
            - 不得捏造任何政策内容，所有结论必须有来源支撑；
            - 搜索结果存疑时，主动交叉验证多个来源；
            - 每次行动必须调用工具函数，禁止仅用文字描述行动意图。
            """;

    private static final String NEXT_STEP_PROMPT = """
            请根据上方已有的搜索结果判断下一步行动，并立即调用对应工具函数执行：

            1. 如果你已经拿到了足够的信息 → 直接调用 generatePDF 生成研究报告（这是默认首选）
            2. 如果信息明显不足且尚未搜索过 → 调用 webSearchAdvanced 搜索
            3. 如果报告已生成 → 调用 doTerminate 结束任务

            注意：必须调用工具函数，禁止仅描述将要做什么。不要重复搜索已有的内容。
            """;

    public ResearchAgent(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("ResearchAgent");
        this.setSystemPrompt(SYSTEM_PROMPT);
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(5); // 搜索→(可选)二次搜索→生成PDF→终止，留出缓冲
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
