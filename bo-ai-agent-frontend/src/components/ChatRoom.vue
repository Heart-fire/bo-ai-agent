<template>
  <div class="chat-container">
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">
        <!-- AI消息 -->
        <template v-if="!msg.isUser">
          <!-- Agent响应（思考过程+结果） -->
          <template v-if="msg.type === 'agent-response'">
            <div class="message ai-message agent-message">
              <div class="avatar ai-avatar">
                <AiAvatarFallback :type="aiType"/>
              </div>
              <div class="message-bubble full-width">
                <ThinkingProcess
                  :steps="msg.content.steps || []"
                  :tool-results="msg.content.toolResults || []"
                  :summary="msg.content.summary || null"
                  :summary-text="msg.content.summaryText || ''"
                  :is-done="msg.content.isDone || false"
                  :elapsed-time="msg.content.elapsedTime || 0"
                />
              </div>
            </div>
          </template>
          <!-- 结构化消息 -->
          <template v-else-if="msg.isStructured">
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
                      class="typing-indicator">
                  <span class="typing-dot"></span>
                </span>
              </div>
            </div>
          </div>
        </template>

        <!-- 用户消息 -->
        <div v-else class="message user-message" :class="[msg.type]">
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
<!--            <div class="message-time">{{ formatTime(msg.time) }}</div>-->
          </div>
          <div class="avatar user-avatar">
            <div class="avatar-placeholder">😎</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 - DeepSeek 风格 -->
    <div class="chat-input-container">
      <div class="chat-input-wrapper">
        <div class="input-actions-left">
<!--          <button class="icon-button" title="上传文件">-->
<!--            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">-->
<!--              <path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/>-->
<!--            </svg>-->
<!--          </button>-->
        </div>

        <div class="input-box-wrapper">
          <textarea
              ref="textareaRef"
              v-model="inputMessage"
              @keydown="handleKeydown"
              @input="handleInput"
              placeholder="有问题，尽管问，Enter 发送，Shift + Enter 换行"
              class="input-box"
              rows="1"
          ></textarea>
        </div>

        <div class="input-actions-right">
          <button
              @click="sendMessage"
              :disabled="!inputMessage.trim()"
              class="send-button"
              :class="{ 'send-button-active': inputMessage.trim() }"
              title="发送 (Enter)"
          >
            <img src="/向上.png" alt="发送" class="send-icon" />
          </button>
        </div>
      </div>
      <div class="input-hint">
        <span>内容由AI生成，仅供参考</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, nextTick, watch, computed} from 'vue'
import AiAvatarFallback from './AiAvatarFallback.vue'
import StructuredMessage from './StructuredMessage.vue'
import ThinkingProcess from './ThinkingProcess.vue'

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
const textareaRef = ref(null)

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

  // 重置 textarea 高度
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = 'auto'
    }
  })
}

// 处理输入事件，自动调整高度
const handleInput = () => {
  const textarea = textareaRef.value
  if (!textarea) return

  // 重置高度以获取正确的 scrollHeight
  textarea.style.height = 'auto'

  // 计算新高度，最小 24px，最大 200px
  const newHeight = Math.min(Math.max(textarea.scrollHeight, 24), 200)
  textarea.style.height = newHeight + 'px'
}

// 处理键盘事件
const handleKeydown = (e) => {
  // Enter 发送，Shift + Enter 换行
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
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
  --accent-blue: #f1e6e6;
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
  padding-bottom: 120px;
  display: flex;
  flex-direction: column;
  position: absolute;
  inset: 0;
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

.agent-message {
  width: 100%;
  max-width: 100%;
}

.agent-message .message-bubble {
  background: transparent;
  border: none;
  padding: 0;
}

.agent-message .message-bubble.full-width {
  width: 100%;
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
  background: linear-gradient(135deg, var(--accent-blue) 0%, #dbdeef 100%);
  //color: white;
  font-size: 18px;
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
  background: linear-gradient(
      135deg,
      #edf3fe 0%,
      #edf3fe 40%,
      #edf3fe 100%
  );
  color: #0f1115;
  padding: 12px 18px;
  border-radius: 20px;
  border-bottom-right-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.user-message .message-bubble:hover {
  transform: translateY(-1px);
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
   输入区域 - DeepSeek / ChatGPT 风格
   ============================================ */

.chat-input-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-light);
  z-index: 100;
  padding: 12px 20px 16px;
}


/* 输入框整体 */
.chat-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;

  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: 16px;

  padding: 8px 12px;

  transition: all 0.2s ease;
}

/* focus 状态 */
.chat-input-wrapper:focus-within {
  border-color: var(--accent-blue);
  background: var(--bg-primary);
  box-shadow: 0 0 0 3px rgba(43, 111, 255, 0.08);
}


/* 输入框容器 */
.input-box-wrapper {
  flex: 1;
  display: flex;
}


.input-box {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  background: transparent;

  font-size: 14px;
  line-height: 22px;

  padding: 6px 0;

  max-height: 160px;
  overflow-y: auto;

  font-family: inherit;

  color: var(--text-primary);
  opacity: 1;
}

.input-box::placeholder {
  color: var(--text-tertiary);
}

/* scrollbar */
.input-box::-webkit-scrollbar {
  width: 4px;
}

.input-box::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.1);
  border-radius: 2px;
}


/* ============================================
   左侧按钮
   ============================================ */

.input-actions-left {
  display: flex;
  align-items: center;
  gap: 4px;
}


/* ============================================
   右侧按钮
   ============================================ */

.input-actions-right {
  display: flex;
  align-items: center;
}


/* ============================================
   发送按钮
   ============================================ */

.send-button {
  width: 36px;
  height: 36px;

  border: none;
  border-radius: 50%;

  background: rgba(43, 111, 255, 0.1);

  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: center;

  transition: all 0.2s ease;
  padding: 0;
}


/* 发送图标 */
.send-button .send-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;

  opacity: 0.5;
  transition: all 0.2s ease;
}


/* disabled */
.send-button:disabled {
  cursor: not-allowed;
  background: rgba(0,0,0,0.05);
}

.send-button:disabled .send-icon {
  opacity: 0.3;
}


/* 激活 */
.send-button-active {
  background: var(--accent-blue);
}

.send-button-active .send-icon {
  opacity: 1;
}


/* hover */
.send-button-active:hover {
  background: var(--accent-blue-hover);
  transform: scale(1.08);
}


/* 点击 */
.send-button:active {
  transform: scale(0.95);
}


/* ============================================
   输入提示
   ============================================ */

.input-hint {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.input-hint span {
  font-size: 11px;
  color: var(--text-tertiary);
}

/* ============================================
   打字指示器
   ============================================ */
.typing-indicator {
  display: inline-block;
  margin-left: 4px;
  vertical-align: middle;
}

.typing-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: var(--accent-blue);
  border-radius: 50%;
  animation: typingPulse 1.4s ease-in-out infinite;
  box-shadow: 0 0 8px rgb(255, 255, 255);
}

@keyframes typingPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.8);
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

  .chat-input-container {
    padding: 10px 16px 14px;
  }

  .chat-input-wrapper {
    padding: 6px 6px 6px 10px;
  }

  .input-box {
    font-size: 15px;
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

  .chat-messages {
    padding-bottom: 100px;
  }

  .input-actions-left .icon-button,
  .send-button {
    width: 32px;
    height: 32px;
  }

  .icon-button svg,
  .send-button .send-icon {
    width: 16px;
    height: 16px;
  }

  .chat-input-container {
    padding: 8px 12px 12px;
  }
}

/* ============================================
   消息类型样式
   ============================================ */

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