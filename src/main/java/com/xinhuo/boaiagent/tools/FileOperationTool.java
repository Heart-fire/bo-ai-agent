package com.xinhuo.boaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.xinhuo.boaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 文件操作工具类（提供文件读写功能）
 */
@Component
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    /**
     * 读取文件内容
     * @param fileName 文件名
     * @return 文件内容
     */
    @Tool(description = "读取文件内容")
    public String readFile(@ToolParam(description = "要读取的文件名") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "读取文件错误: " + e.getMessage();
        }
    }

    /**
     * 写入内容到文件
     * @param fileName 文件名
     * @param content 要写入的内容
     * @return 操作结果
     */
    @Tool(description = "写入内容到文件")
    public String writeFile(@ToolParam(description = "要写入的文件名") String fileName,
                            @ToolParam(description = "要写入文件的内容") String content
    ) {
        String filePath = FILE_DIR + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "写入成功的文件: " + filePath;
        } catch (Exception e) {
            return "写入文件错误: " + e.getMessage();
        }
    }
}