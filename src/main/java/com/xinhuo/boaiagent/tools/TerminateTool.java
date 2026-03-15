package com.xinhuo.boaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 终止工具（作用是让自主规划智能体能够合理地中断）
 */
@Component
public class TerminateTool {

    @Tool(description = """
    End the task immediately.

    Use this tool when:
    1. The user request has been fully answered.
    2. You cannot find more useful information.
    3. Further searching will not help.

    Always call this tool when the task is complete.
    """)
    public String doTerminate() {
        return "任务结束";
    }
}