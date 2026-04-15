package com.xinhuo.boaiagent.chatMemory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话记忆持久化实体
 */
@Data
@TableName(value = "chat_memory")
public class ChatMemoryEntity {

    @TableId(type = IdType.INPUT)
    private String conversationId;

    private byte[] messages;

    private String messageText;

    private Integer messageCount;

    private LocalDateTime updatedAt;
}
