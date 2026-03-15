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

    @Tool(description = """
        Read the content of a text file from the workspace directory.
        Use this tool when you need to read previously saved information or intermediate results.
        Only provide the file name, not the full path.
        """)
    public String readFile(
            @ToolParam(description = "The name of the file to read, e.g. result.txt")
            String fileName) {

        String filePath = FILE_DIR + "/" + fileName;

        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = """
        Write text content into a file in the workspace directory.
        Use this tool to save intermediate results or final outputs.
        """)
    public String writeFile(
            @ToolParam(description = "Name of the file, for example result.txt")
            String fileName,

            @ToolParam(description = "The text content to write into the file")
            String content
    ) {

        String filePath = FILE_DIR + "/" + fileName;

        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}