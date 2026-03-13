package com.xinhuo.boaiagent.tools;

import com.xinhuo.boaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 集中的工具注册类
 */
@Configuration
public class ToolRegistration {

    @Resource
    private WebSearchTool webSearchTool;

    @Resource
    private FileOperationTool  fileOperationTool;

    @Resource
    private WebScrapingTool webScrapingTool;

    @Resource
    private ResourceDownloadTool resourceDownloadTool;

    @Resource
    private TerminalOperationTool terminalOperationTool;

    @Resource
    private PDFGenerationTool pdfGenerationTool;

    @Resource
    private TerminateTool terminateTool;

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool
        );
    }
}