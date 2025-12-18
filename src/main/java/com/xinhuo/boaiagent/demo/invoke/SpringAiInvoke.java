package com.xinhuo.boaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: Wangcb
 * @Description: Spring AI 框架调用AI大模型
 * @Version: 1.0
 */
@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private StreamingChatModel dashscopeStreamingChatModel;
    @Override
    public void run(String... args) {

        dashscopeStreamingChatModel
                .stream(new Prompt("你好"))
                .doOnNext(resp -> {
                    System.out.print(
                            resp.getResult().getOutput().getText()
                    );
                })
                .blockLast();
    }
}
