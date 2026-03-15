package com.xinhuo.boaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.xinhuo.boaiagent.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 资源下载工具
 */
@Slf4j
@Component
public class ResourceDownloadTool {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Tool(description = "Download a file or image from a URL and save it locally")
    public String downloadResource(
            @ToolParam(description = "URL of the resource") String url,
            @ToolParam(description = "File name to save the resource") String fileName) {

        if (!url.startsWith("http")) {
            return "Invalid URL. Only HTTP/HTTPS supported.";
        }

        // 清理文件名
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;

        try {

            FileUtil.mkdir(fileDir);

            log.info("Downloading resource: {}", url);

            HttpResponse response = HttpRequest.get(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(30000)
                    .execute();

            if (response.getStatus() != 200) {
                return "Download failed. HTTP status: " + response.getStatus();
            }

            byte[] data = response.bodyBytes();

            if (data.length > MAX_FILE_SIZE) {
                return "File too large. Max allowed size is 10MB.";
            }

            File file = new File(filePath);
            FileUtil.writeBytes(data, file);

            return """
                    Download completed successfully.

                    File Path: %s
                    File Size: %d bytes
                    """.formatted(filePath, file.length());

        } catch (Exception e) {
            log.error("Download failed", e);
            return "Download error: " + e.getMessage();
        }
    }
}