-- 对话记忆持久化表（PostgreSQL）
CREATE TABLE IF NOT EXISTS chat_memory (
    conversation_id VARCHAR(64) PRIMARY KEY,
    messages        BYTEA         NOT NULL,
    message_text    TEXT          NOT NULL DEFAULT '',
    message_count   INT           NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_memory_updated_at ON chat_memory (updated_at);
