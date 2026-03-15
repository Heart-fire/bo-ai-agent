package com.xinhuo.boaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 终端操作工具
 */
@Component
public class TerminalOperationTool {

    private static final Set<String> ALLOWED_COMMANDS = Set.of(
            "dir",
            "echo",
            "ipconfig",
            "whoami"
    );

    /**
     * 执行安全终端命令并返回输出
     * @param command
     * @return
     */
    @Tool(description = "Execute a safe terminal command and return the output")
    public String executeTerminalCommand(
            @ToolParam(description = "Command to execute in terminal") String command) {

        String baseCommand = command.split(" ")[0];

        if (!ALLOWED_COMMANDS.contains(baseCommand)) {
            return "Command not allowed: " + baseCommand;
        }

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String line;

            while ((line = stdout.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = stderr.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);

            if (!finished) {
                process.destroy();
                return "Command timed out.";
            }

            return output.toString();

        } catch (Exception e) {
            return "Command execution failed: " + e.getMessage();
        }
    }
}