<template>
  <div class="chat-container">
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">
        <!-- AI消息 -->
        <template v-if="!msg.isUser">
          <!-- 结构化消息 -->
          <template v-if="msg.isStructured">
            <StructuredMessage :message="msg.content"/>
          </template>
          <!-- 普通文本消息 -->
          <div v-else class="message ai-message" :class="[msg.type]">
            <div class="avatar ai-avatar">
              <AiAvatarFallback :type="aiType"/>
            </div>
            <div class="message-bubble">
              <div class="message-content">
                {{ msg.content }}
                <span v-if="connectionStatus === 'connecting' && index === messages.length - 1"
                      class="typing-indicator">▋</span>
              </div>
              <div class="message-time">{{ formatTime(msg.time) }}</div>
            </div>
          </div>
        </template>

        <!-- 用户消息 -->
        <div v-else class="message user-message" :class="[msg.type]">
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.time) }}</div>
          </div>
          <div class="avatar user-avatar">
            <div class="avatar-placeholder">我</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-container">
      <div class="chat-input">
        <textarea
            v-model="inputMessage"
            @keydown="handleKeydown"
            @input="autoResize"
            placeholder="请输入消息..."
            class="input-box"
        ></textarea>
        <button
            @click="sendMessage"
            :disabled="!inputMessage.trim()"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, nextTick, watch, computed} from 'vue'
import AiAvatarFallback from './AiAvatarFallback.vue'
import StructuredMessage from './StructuredMessage.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  connectionStatus: {
    type: String,
    default: 'disconnected'
  },
  aiType: {
    type: String,
    default: 'default'  // 'love' 或 'super'
  }
})

const emit = defineEmits(['send-message'])

const inputMessage = ref('')
const messagesContainer = ref(null)

// 根据AI类型选择不同头像
const aiAvatar = computed(() => {
  return props.aiType === 'love'
      ? '/ai-love-avatar.png'  // 恋爱大师头像
      : '/ai-super-avatar.png' // 超级智能体头像
})

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  emit('send-message', inputMessage.value)
  inputMessage.value = ''
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'})
}

// 自动滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听消息变化与内容变化，自动滚动
watch(() => props.messages.length, () => {
  scrollToBottom()
})

watch(() => props.messages.map(m => m.content).join(''), () => {
  scrollToBottom()
})

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
/* ============================================
   CSS 变量系统
   ============================================ */
.chat-container {
  --bg-primary: #ffffff;
  --bg-secondary: #f7f7f8;
  --bg-tertiary: #f0f0f1;
  --text-primary: #1a1a1a;
  --text-secondary: #6b7280;
  --text-tertiary: #9ca3af;
  --accent-blue: #2b6fff;
  --accent-blue-hover: #1e5fe4;
  --border-light: #e5e7eb;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.04);
  --shadow-md: 0 4px 12px 0 rgb(0 0 0 / 0.06);

  display: flex;
  flex-direction: column;
  height: 70vh;
  min-height: 600px;
  background: var(--bg-primary);
  border-radius: 16px;
  overflow: hidden;
  position: relative;
}

/* ============================================
   消息区域
   ============================================ */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  padding-bottom: 80px;
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 72px;
  scroll-behavior: smooth;
}

/* 自定义滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* ============================================
   消息包装器
   ============================================ */
.message-wrapper {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  width: 100%;
  animation: messageEnter 0.3s ease-out;
}

@keyframes messageEnter {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============================================
   消息样式
   ============================================ */
.message {
  display: flex;
  align-items: flex-start;
  max-width: 80%;
  gap: 10px;
}

.user-message {
  margin-left: auto;
  flex-direction: row-reverse;
}

.ai-message {
  margin-right: auto;
}

/* ============================================
   头像
   ============================================ */
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
}

.user-avatar {
  margin-left: 0;
}

.ai-avatar {
  margin-right: 0;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--accent-blue) 0%, #1e40af 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
}

/* ============================================
   消息气泡
   ============================================ */
.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  position: relative;
  word-wrap: break-word;
  max-width: 100%;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, var(--accent-blue) 0%, #1e40af 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: var(--bg-secondary);
  color: var(--text-primary);
  border-bottom-left-radius: 4px;
  border: 1px solid var(--border-light);
}

.message-content {
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.message-time {
  font-size: 11px;
  opacity: 0.6;
  margin-top: 6px;
  text-align: right;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.ai-message .message-time {
  color: var(--text-tertiary);
}

/* ============================================
   输入区域
   ============================================ */
.chat-input-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;

  background: var(--bg-primary);
  border-top: 1px solid var(--border-light);

  z-index: 100;
  height: 80px;

  box-shadow: 0 -6px 24px rgba(0,0,0,0.05);
}


/* 输入区域布局 */

.chat-input {
  display: flex;
  align-items: center;
  gap: 12px;

  padding: 16px 20px;
  height: 100%;
  box-sizing: border-box;
}



/* 输入框 */

.input-box {
  flex: 1;

  border: 1.5px solid var(--border-light);
  border-radius: 14px;

  padding: 12px 16px;

  font-size: 15px;
  font-family: inherit;

  resize: none;
  outline: none;

  min-height: 22px;
  max-height: 120px;

  overflow-y: auto;

  background: var(--bg-secondary);

  /* 关键：明确文本颜色 */
  color: var(--text-primary);

  transition: all 0.2s ease;
}


/* placeholder */

.input-box::placeholder {
  color: var(--text-tertiary);
  opacity: 1;
}


/* focus效果 */

.input-box:focus {
  border-color: var(--accent-blue);
  background: var(--bg-primary);

  box-shadow: 0 0 0 3px rgba(43,111,255,0.12);
}


/* 滚动条隐藏 */

.input-box::-webkit-scrollbar {
  width: 6px;
}

.input-box::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.1);
  border-radius: 4px;
}



/* 发送按钮 */

.send-button {
  background: var(--accent-blue);
  color: white;

  border: none;
  border-radius: 12px;

  padding: 0 22px;

  font-size: 14px;
  font-weight: 600;

  cursor: pointer;

  height: 44px;

  display: flex;
  align-items: center;
  justify-content: center;

  transition: all 0.2s ease;
}


.send-button:hover {
  background: #1f5fff;
  transform: translateY(-1px);
}


.send-button:active {
  transform: scale(0.96);
}


.send-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}



/* disabled输入框 */

.input-box:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ============================================
   打字指示器
   ============================================ */
.typing-indicator {
  display: inline-block;
  animation: cursorBlink 1s step-end infinite;
  margin-left: 2px;
  color: var(--accent-blue);
}

@keyframes cursorBlink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

/* ============================================
   响应式设计
   ============================================ */
@media (max-width: 768px) {
  .chat-messages {
    padding: 16px;
  }

  .message {
    max-width: 90%;
  }

  .chat-input {
    padding: 12px 16px;
  }

  .input-box {
    font-size: 16px;
  }

  .send-button {
    padding: 0 16px;
    font-size: 13px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .avatar {
    width: 28px;
    height: 28px;
    border-radius: 10px;
  }

  .avatar-placeholder {
    font-size: 12px;
  }

  .message-bubble {
    padding: 10px 14px;
  }

  .message-content {
    font-size: 14px;
  }

  .chat-input-container {
    height: 64px;
  }

  .chat-messages {
    bottom: 64px;
  }

  .send-button {
    padding: 0 14px;
    height: 36px;
  }

  .input-box {
    padding: 8px 12px;
  }
}

/* ============================================
   消息类型样式
   ============================================ */
.ai-answer {
  animation: fadeIn 0.3s ease-in-out;
}

.ai-final {
  border-left: 2px solid var(--accent-blue);
}

.ai-error {
  opacity: 0.7;
}

/* ============================================
   连续消息优化
   ============================================ */
.ai-message + .ai-message .avatar {
  visibility: hidden;
}

.ai-message + .ai-message .message-bubble {
  border-top-left-radius: 12px;
}

.user-message + .user-message .avatar {
  visibility: hidden;
}

.user-message + .user-message .message-bubble {
  border-top-right-radius: 12px;
}

/* ============================================
   淡入动画
   ============================================ */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style> 