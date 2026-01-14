package com.xinhuo.boaiagent;

import com.xinhuo.boaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChatStream() {
        String chatId = UUID.randomUUID().toString();

        Flux<String> flux = loveApp.doChatStream("你好，我是xxx", chatId);

        List<String> result = flux
                .collectList()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());

        String fullText = String.join("", result);
        System.out.println(fullText);
    }
}
