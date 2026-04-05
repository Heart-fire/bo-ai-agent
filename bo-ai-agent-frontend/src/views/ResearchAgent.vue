<template>
  <div class="chat-page">

    <!-- 顶部微弱日出光晕（紫色调，与 Agent 主题呼应） -->
    <div class="ambient-bg" aria-hidden="true">
      <div class="glow-a"></div>
      <div class="glow-b"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="top-bar">
      <div class="top-bar-left">
        <div class="brand-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10"/>
            <line x1="12" y1="20" x2="12" y2="4"/>
            <line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
        </div>
        <span class="brand-name">政策通</span>
        <span class="divider">/</span>
        <div class="mode-badge agent-badge">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10"/>
            <line x1="12" y1="20" x2="12" y2="4"/>
            <line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
          政策分析 Agent
        </div>
      </div>
      <div class="top-bar-right">
        <button class="nav-btn" @click="handleNewChat">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          新对话
        </button>
        <button class="nav-btn" @click="goBack">首页</button>
      </div>
    </header>

    <!-- 消息列表 -->
    <div class="messages-scroll" ref="scrollContainer" @scroll="handleScroll">
      <div class="messages-inner">

        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="20" x2="18" y2="10"/>
              <line x1="12" y1="20" x2="12" y2="4"/>
              <line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
          </div>
          <p class="empty-title">政策分析 Agent</p>
          <p class="empty-subtitle">深度分析政策，生成结构化报告</p>
        </div>

        <!-- 消息列表 -->
        <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">

          <!-- 用户消息 -->
          <div v-if="msg.isUser" class="message user-message">
            <div class="message-bubble user-bubble">{{ msg.content }}</div>
            <div class="user-avatar">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
          </div>

          <!-- AI Agent 消息 -->
          <div v-else class="message ai-message group">
            <div class="ai-avatar agent-ai-avatar">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="20" x2="18" y2="10"/>
                <line x1="12" y1="20" x2="12" y2="4"/>
                <line x1="6" y1="20" x2="6" y2="14"/>
              </svg>
            </div>
            <div class="ai-content">

              <!-- Agent 类型消息：思考过程 + 报告 -->
              <template v-if="msg.type === 'agent-response'">
                <div class="ai-bubble">
                  <ThinkingProcess
                    :steps="msg.content.steps || []"
                    :tool-results="msg.content.toolResults || []"
                    :summary="msg.content.summary || null"
                    :summary-text="msg.content.summaryText || ''"
                    :is-done="msg.content.isDone || false"
                    :elapsed-time="msg.content.elapsedTime || 0"
                  />
                </div>
              </template>

              <!-- 结构化消息 -->
              <template v-else-if="msg.isStructured">
                <div class="ai-bubble">
                  <StructuredMessage :message="msg.content" />
                </div>
              </template>

              <!-- 普通文本消息（欢迎语等） -->
              <template v-else>
                <div class="ai-bubble">
                  <div class="plain-text">{{ msg.content }}</div>
                  <span
                    v-if="connectionStatus === 'connecting' && index === messages.length - 1"
                    class="typing-cursor"
                  ></span>
                </div>
              </template>

              <!-- 复制按钮 -->
              <div
                class="action-bar"
                v-if="!msg.isUser && msg.content && typeof msg.content === 'string' && !(connectionStatus === 'connecting' && index === messages.length - 1)"
              >
                <button class="action-btn" @click="copyMessage(msg.content, index)">
                  <svg v-if="copiedIndex !== index" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                  </svg>
                  <svg v-else width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:#16a34a">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  {{ copiedIndex === index ? '已复制' : '复制' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div ref="messagesEnd"></div>
      </div>
    </div>

    <!-- 滚动到底部按钮 -->
    <button v-if="showScrollDown" class="scroll-down-btn" @click="scrollToBottom">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="6 9 12 15 18 9"/>
      </svg>
    </button>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-container">
        <div class="input-box" :class="{ focused: isInputFocused }">
          <textarea
            ref="textareaRef"
            v-model="inputMessage"
            @keydown="handleKeydown"
            @input="handleInput"
            @focus="isInputFocused = true"
            @blur="isInputFocused = false"
            placeholder="输入政策研究任务，Agent 将自主搜索并生成报告…"
            :disabled="connectionStatus === 'connecting'"
            rows="1"
            class="input-textarea"
          ></textarea>
          <div class="input-bottom">
            <span class="input-hint-text">
              <template v-if="connectionStatus === 'connecting'">
                <span class="generating-indicator">
                  <svg class="spin-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                  </svg>
                  Agent 正在研究…
                </span>
              </template>
              <template v-else>Enter 发送 · Shift+Enter 换行</template>
            </span>
            <button
              class="send-btn"
              :class="{ 'send-active': inputMessage.trim() && connectionStatus !== 'connecting' }"
              @click="sendMessage"
              :disabled="!inputMessage.trim() || connectionStatus === 'connecting'"
            >
              <img src="@/assets/025-发送.png" width="18" height="18" alt="发送" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ThinkingProcess from '../components/ThinkingProcess.vue'
import StructuredMessage from '../components/StructuredMessage.vue'
import { chatWithResearchAgent } from '../api'
import { parseMessage, MessageType } from '../types/structured-message'

useHead({
  title: '政策分析 Agent - 北京政策智能助手平台',
  meta: [
    { name: 'description', content: '自主搜索公开信息、生成政策研究报告，支持积分落户、社保、交通等多类政策' },
    { name: 'keywords', content: '政策研究,信息采集,Agent,北京政策,研究报告' }
  ]
})

const router = useRouter()

// ── 状态 ──────────────────────────────────────────────────
const messages = ref([])
const connectionStatus = ref('disconnected')
const inputMessage = ref('')
const isInputFocused = ref(false)
const copiedIndex = ref(-1)
const showScrollDown = ref(false)

const scrollContainer = ref(null)
const messagesEnd = ref(null)
const textareaRef = ref(null)

let eventSource = null
let agentResponseIndex = -1
let requestStartTime = 0

// ── API 调用逻辑（保留原有）────────────────────────────────
const addMessage = (content, isUser, type = '', isStructured = false) => {
  messages.value.push({ content, isUser, type, isStructured, time: new Date().getTime() })
}

const ensureAgentResponse = () => {
  if (agentResponseIndex < 0 || agentResponseIndex >= messages.value.length) {
    messages.value.push({
      content: { steps: [], toolResults: [], summary: null, summaryText: '', isDone: false, elapsedTime: 0 },
      isUser: false,
      type: 'agent-response',
      isStructured: false,
      time: new Date().getTime()
    })
    agentResponseIndex = messages.value.length - 1
  }
  return messages.value[agentResponseIndex].content
}

const sendMessage = () => {
  if (!inputMessage.value.trim() || connectionStatus.value === 'connecting') return

  const text = inputMessage.value.trim()
  addMessage(text, true, 'user-question')
  inputMessage.value = ''
  agentResponseIndex = -1
  requestStartTime = Date.now()

  nextTick(() => {
    if (textareaRef.value) textareaRef.value.style.height = 'auto'
  })

  if (eventSource) eventSource.close()

  connectionStatus.value = 'connecting'
  eventSource = chatWithResearchAgent(text)

  eventSource.onmessage = (event) => {
    const data = event.data
    if (data === '[DONE]') {
      const agentContent = ensureAgentResponse()
      agentContent.isDone = true
      agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      connectionStatus.value = 'disconnected'
      eventSource.close()
      return
    }
    const parsed = parseMessage(data)
    if (parsed === null) return
    if (typeof parsed === 'object' && parsed.messageType) {
      const agentContent = ensureAgentResponse()
      if (parsed.messageType === MessageType.STEP && parsed.data) {
        agentContent.steps.push(parsed.data)
      } else if (parsed.messageType === MessageType.TOOL_RESULT && parsed.data) {
        agentContent.toolResults.push(...parsed.data)
      } else if (parsed.messageType === MessageType.SUMMARY && parsed.data) {
        agentContent.summary = parsed.data
      } else if (parsed.messageType === MessageType.SUMMARY_STREAM && parsed.data) {
        agentContent.summaryText = (agentContent.summaryText || '') + parsed.data.content
      } else if (parsed.messageType === MessageType.SUMMARY_DONE) {
        agentContent.isDone = true
        agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      } else if (parsed.messageType === MessageType.ERROR && parsed.data) {
        agentContent.error = parsed.data
        agentContent.isDone = true
        agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      }
      connectionStatus.value = parsed.messageType === MessageType.ERROR ? 'error' : 'connected'
    }
    nextTick(() => scrollToBottom())
  }

  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    const agentContent = ensureAgentResponse()
    agentContent.isDone = true
    agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
    connectionStatus.value = 'error'
    eventSource.close()
  }

  nextTick(() => scrollToBottom())
}

// ── 键盘 / 高度 ────────────────────────────────────────────
const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const handleInput = () => {
  const ta = textareaRef.value
  if (!ta) return
  ta.style.height = 'auto'
  ta.style.height = Math.min(ta.scrollHeight, 160) + 'px'
}

// ── 滚动 ──────────────────────────────────────────────────
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesEnd.value) messagesEnd.value.scrollIntoView({ behavior: 'smooth' })
  })
}

const handleScroll = () => {
  const sc = scrollContainer.value
  if (sc) showScrollDown.value = sc.scrollHeight - sc.scrollTop - sc.clientHeight > 300
}

// ── 复制 ──────────────────────────────────────────────────
const copyMessage = (content, index) => {
  navigator.clipboard.writeText(content)
  copiedIndex.value = index
  setTimeout(() => { copiedIndex.value = -1 }, 2000)
}

// ── 导航 ──────────────────────────────────────────────────
const goBack = () => router.push('/')

const handleNewChat = () => {
  if (eventSource) eventSource.close()
  messages.value = []
  connectionStatus.value = 'disconnected'
  agentResponseIndex = -1
  addMessage('你好，我是信息采集研究员。我可以帮你自动搜索、整理公开信息并生成研究报告。请描述你想要研究的主题。', false)
}

// ── 生命周期 ──────────────────────────────────────────────
onMounted(() => {
  addMessage('你好，我是信息采集研究员。我可以帮你自动搜索、整理公开信息并生成研究报告。请描述你想要研究的主题。', false)
})

onBeforeUnmount(() => {
  if (eventSource) eventSource.close()
})
</script>

<style scoped>
/* ── 容器 ── */
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
  color: #0f172a;
  overflow: hidden;
  position: relative;
}

/* ── 光晕（紫色调，极淡） ── */
.ambient-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.glow-a {
  position: absolute;
  top: 0; left: 0;
  width: 520px; height: 520px;
  background: radial-gradient(circle at 15% 15%,
    rgba(139,92,246,0.07) 0%, rgba(109,40,217,0.04) 45%, transparent 70%);
  filter: blur(40px);
}
.glow-b {
  position: absolute;
  top: 0; left: 0;
  width: 320px; height: 320px;
  background: radial-gradient(circle at 10% 10%,
    rgba(167,139,250,0.06) 0%, transparent 65%);
  filter: blur(30px);
}

/* ── 顶部导航 ── */
.top-bar {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-bottom: 1px solid #f1f5f9;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  flex-shrink: 0;
}

.top-bar-left { display: flex; align-items: center; gap: 12px; }

.brand-icon {
  width: 28px; height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  color: white;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.25);
}

.brand-name { font-size: 14px; font-weight: 500; color: #334155; }
.divider    { font-size: 14px; color: #cbd5e1; }

.mode-badge {
  display: flex; align-items: center; gap: 6px;
  padding: 4px 12px; border-radius: 100px;
  font-size: 12px; font-weight: 500;
}
.agent-badge {
  background: #f5f3ff;
  border: 1px solid #ddd6fe;
  color: #6d28d9;
}

.top-bar-right { display: flex; align-items: center; gap: 4px; }

.nav-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 12px; border-radius: 8px;
  background: transparent; border: none;
  color: #64748b; font-size: 13px; cursor: pointer;
  transition: all 0.15s ease;
}
.nav-btn:hover { background: #f1f5f9; color: #0f172a; }

/* ── 消息区 ── */
.messages-scroll {
  position: relative;
  z-index: 5;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background: linear-gradient(to bottom, rgba(245,243,255,0.35) 0%, rgba(255,255,255,0) 200px);
}
.messages-scroll::-webkit-scrollbar { width: 4px; }
.messages-scroll::-webkit-scrollbar-track { background: transparent; }
.messages-scroll::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }

.messages-inner {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 20px;
}

/* ── 空状态 ── */
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; height: 260px; text-align: center;
  user-select: none;
}

.empty-icon {
  width: 56px; height: 56px;
  border-radius: 18px;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  color: white; margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2);
}

.empty-title   { font-size: 16px; font-weight: 600; color: #0f172a; margin: 0 0 6px; }
.empty-subtitle{ font-size: 14px; color: #94a3b8; margin: 0; }

/* ── 消息条目 ── */
.message-wrapper {
  margin-bottom: 24px;
  animation: msgEnter 0.25s ease-out;
}

@keyframes msgEnter {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

.message { display: flex; align-items: flex-end; gap: 10px; }

/* 用户消息 */
.user-message { justify-content: flex-end; }

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  max-width: 75%;
  font-size: 0.9rem; line-height: 1.65;
  word-break: break-word;
}

.user-bubble {
  background: #0f172a; color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.user-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex; align-items: center; justify-content: center;
  color: #64748b; flex-shrink: 0;
}

/* AI 消息 */
.ai-message { justify-content: flex-start; align-items: flex-start; }

.ai-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0; margin-top: 4px;
}

.agent-ai-avatar {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.2);
}

.ai-content { flex: 1; min-width: 0; }

.ai-bubble {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 18px; border-top-left-radius: 4px;
  padding: 16px 20px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05);
  color: #334155; font-size: 0.87rem; line-height: 1.7;
}

.plain-text { white-space: pre-wrap; }

.typing-cursor {
  display: inline-block; width: 2px; height: 16px;
  background: #8b5cf6; margin-left: 2px;
  vertical-align: middle;
  animation: blink 1s step-end infinite;
  border-radius: 1px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0; }
}

/* 操作栏 */
.action-bar {
  display: flex; gap: 4px; margin-top: 10px;
  opacity: 0; transition: opacity 0.15s ease;
}
.ai-message:hover .action-bar { opacity: 1; }

.action-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 4px 8px; border-radius: 6px;
  background: transparent; border: none;
  color: #94a3b8; font-size: 12px; cursor: pointer;
  transition: all 0.15s ease;
}
.action-btn:hover { background: #f1f5f9; color: #475569; }

/* ── 滚动到底部 ── */
.scroll-down-btn {
  position: fixed; bottom: 100px; right: 24px;
  width: 36px; height: 36px; border-radius: 50%;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #64748b;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: all 0.15s ease; z-index: 20;
}
.scroll-down-btn:hover { background: #f8fafc; color: #0f172a; box-shadow: 0 6px 18px rgba(0,0,0,0.12); }

/* ── 输入区域 ── */
.input-area {
  position: relative;
  z-index: 10;
  flex-shrink: 0;
  padding: 12px 20px 20px;
  background: #fff;
  border-top: 1px solid #f1f5f9;
}

.input-container { max-width: 800px; margin: 0 auto; }

.input-box {
  position: relative;
  border-radius: 18px;
  border: 1px solid #e2e8f0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.input-box.focused {
  border-color: #c4b5fd;
  box-shadow: 0 0 0 3px rgba(139,92,246,0.08), 0 8px 24px rgba(139,92,246,0.1);
}

.input-textarea {
  width: 100%; background: transparent; border: none; outline: none; resize: none;
  color: #0f172a; font-size: 0.9rem; line-height: 1.65;
  padding: 16px 20px 52px; font-family: inherit;
  max-height: 160px; overflow-y: auto; box-sizing: border-box;
}
.input-textarea::placeholder { color: #94a3b8; }
.input-textarea:disabled { opacity: 0.5; }
.input-textarea::-webkit-scrollbar { width: 4px; }
.input-textarea::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }

.input-bottom {
  position: absolute; bottom: 10px; left: 14px; right: 14px;
  display: flex; align-items: center; justify-content: space-between;
}

.input-hint-text { font-size: 12px; color: #94a3b8; }

.generating-indicator {
  display: flex; align-items: center; gap: 6px;
  color: #8b5cf6;
}

.spin-icon { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.send-btn {
  width: 32px; height: 32px; border-radius: 10px;
  border: none; background: #f1f5f9; color: #cbd5e1;
  display: flex; align-items: center; justify-content: center;
  cursor: not-allowed; transition: all 0.15s ease;
  flex-shrink: 0;
}
.send-btn.send-active {
  background: #8b5cf6; color: white; cursor: pointer;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.30);
}
/* 有输入时图标变白色（亮色） */
.send-btn.send-active img {
  filter: brightness(0) invert(1);
}
.send-btn.send-active:hover { background: #7c3aed; }

/* ── 响应式 ── */
@media (max-width: 768px) {
  .top-bar { padding: 10px 12px; }
  .messages-inner { padding: 20px 12px; }
  .input-area { padding: 10px 12px 16px; }
  .brand-name, .divider { display: none; }
}

@media (max-width: 480px) {
  .top-bar { padding: 8px 10px; }
  .messages-inner { padding: 16px 10px; }
}
</style>
