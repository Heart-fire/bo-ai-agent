package com.xinhuo.boaiagent.agent.model;

/**
 * SSE 连接已关闭时抛出的运行时异常
 * 用于统一收口 SSE 发送过程中的 IOException / IllegalStateException
 */
public class SseClosedException extends RuntimeException {
    public SseClosedException(Throwable cause) {
        super(cause);
    }
}
