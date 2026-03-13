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

    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(
            @ToolParam(description = "URL of the resource to download") String url,
            @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(fileDir);

            log.info("开始下载资源: {}", url);

            // 使用 HttpRequest 添加 headers 并下载
            HttpResponse response = HttpRequest.get(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "image/*,*/*;q=0.9")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .timeout(30000)
                    .execute();

            // 检查响应状态
            int status = response.getStatus();
            log.info("下载响应状态: {}", status);

            if (status != 200) {
                return "下载失败，HTTP状态码: " + status + ", 响应内容: " + response.body();
            }

            // 保存文件
            File file = new File(filePath);
            FileUtil.writeBytes(response.bodyBytes(), file);

            return "资源下载成功: " + filePath + "，大小: " + file.length() + " 字节";

        } catch (Exception e) {
            log.error("下载资源失败: {}", url, e);
            return "下载失败: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}