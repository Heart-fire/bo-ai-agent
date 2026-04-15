package com.xinhuo.boaiagent.chatMemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于 PostgreSQL + Redis + Kryo 的高性能对话记忆持久化方案
 * 写入：Redis 缓存 + PostgreSQL 持久化（同步双写）
 * 读取：先查 Redis，miss 则查 PostgreSQL 并回填 Redis
 * 删除：Redis + PostgreSQL 双删
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PgRedisChatMemory implements ChatMemory {

    private final ChatMemoryMapper chatMemoryMapper;
    private final RedisTemplate<String, byte[]> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "chat:memory:";
    private static final long REDIS_TTL_HOURS = 24;

    private static final Pool<Kryo> kryoPool = new Pool<>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.register(ArrayList.class, 10);
            kryo.register(Collections.emptyList().getClass(), 11);
            kryo.register(SystemMessage.class, 20);
            kryo.register(UserMessage.class, 21);
            kryo.register(AssistantMessage.class, 22);
            return kryo;
        }
    };

    private Kryo obtainKryo() {
        return kryoPool.obtain();
    }

    private void freeKryo(Kryo kryo) {
        kryoPool.free(kryo);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        // 先获取已有消息，再追加新消息
        List<Message> allMessages = get(conversationId);
        allMessages.addAll(messages);

        // Kryo 序列化
        byte[] data = serialize(allMessages);

        // 写 Redis 缓存-存 byte[]
        String redisKey = REDIS_KEY_PREFIX + conversationId;
        redisTemplate.opsForValue().set(redisKey, data, REDIS_TTL_HOURS, TimeUnit.HOURS);

        // 写 PostgreSQL 持久化（upsert）
        String messageText = toReadableText(allMessages);
        saveToPg(conversationId, data, allMessages.size(), messageText);

        log.debug("对话记忆已保存: conversationId={}, 消息数={}", conversationId, allMessages.size());
    }

    @Override
    public List<Message> get(String conversationId) {
        // 查 Redis
        String redisKey = REDIS_KEY_PREFIX + conversationId;
        byte[] cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            List<Message> messages = deserialize(cached);
            log.debug("Redis 缓存命中: conversationId={}, 消息数={}", conversationId, messages.size());
            return messages;
        }

        // Redis miss，查 PostgreSQL
        ChatMemoryEntity entity = chatMemoryMapper.selectById(conversationId);
        if (entity != null && entity.getMessages() != null) {
            List<Message> messages = deserialize(entity.getMessages());

            // 回填 Redis
            redisTemplate.opsForValue().set(redisKey, entity.getMessages(), REDIS_TTL_HOURS, TimeUnit.HOURS);

            log.debug("PostgreSQL 回填 Redis: conversationId={}, 消息数={}", conversationId, messages.size());
            return messages;
        }

        // 全新对话
        return new ArrayList<>();
    }

    @Override
    public void clear(String conversationId) {
        // 删 Redis
        redisTemplate.delete(REDIS_KEY_PREFIX + conversationId);

        // 删 PostgreSQL
        chatMemoryMapper.deleteById(conversationId);

        log.debug("对话记忆已清除: conversationId={}", conversationId);
    }

    /**
     * Upsert 到 PostgreSQL
     */
    private void saveToPg(String conversationId, byte[] data, int messageCount, String messageText) {
        ChatMemoryEntity existing = chatMemoryMapper.selectById(conversationId);
        if (existing == null) {
            ChatMemoryEntity entity = new ChatMemoryEntity();
            entity.setConversationId(conversationId);
            entity.setMessages(data);
            entity.setMessageText(messageText);
            entity.setMessageCount(messageCount);
            entity.setUpdatedAt(LocalDateTime.now());
            chatMemoryMapper.insert(entity);
        } else {
            existing.setMessages(data);
            existing.setMessageText(messageText);
            existing.setMessageCount(messageCount);
            existing.setUpdatedAt(LocalDateTime.now());
            chatMemoryMapper.updateById(existing);
        }
    }

    /**
     * 将消息列表转为可读文本
     */
    private String toReadableText(List<Message> messages) {
        return messages.stream()
                .map(m -> "[" + m.getMessageType().getValue() + "] " + m.getText())
                .collect(Collectors.joining("\n"));
    }

    // ==================== Kryo 序列化 ====================

    private byte[] serialize(List<Message> messages) {
        Kryo kryo = obtainKryo();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            kryo.writeObject(output, messages);
            output.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Kryo 序列化失败", e);
            throw new RuntimeException("对话记忆序列化失败", e);
        } finally {
            freeKryo(kryo);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> deserialize(byte[] data) {
        Kryo kryo = obtainKryo();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             Input input = new Input(bis)) {
            return kryo.readObject(input, ArrayList.class);
        } catch (Exception e) {
            log.error("Kryo 反序列化失败", e);
            throw new RuntimeException("对话记忆反序列化失败", e);
        } finally {
            freeKryo(kryo);
        }
    }
}
