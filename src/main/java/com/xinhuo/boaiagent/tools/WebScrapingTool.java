package com.xinhuo.boaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 网页抓取工具
 */
@Component
public class WebScrapingTool {

    @Tool(description = "Extract the main readable text content from a webpage URL. Use this when detailed information from a webpage is required.")
    public String scrapeWebPage(
            @ToolParam(description = "URL of the web page to scrape") String url) {

        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            // 移除无用标签
            document.select("script, style, nav, footer, header, aside").remove();

            String title = document.title();
            String text = document.body().text();

            // 限制长度，避免 token 爆炸
            if (text.length() > 8000) {
                text = text.substring(0, 8000);
            }

            return """
                    网页抓取成功。

                    Title:
                    %s

                    Content:
                    %s
                    """.formatted(title, text);

        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}