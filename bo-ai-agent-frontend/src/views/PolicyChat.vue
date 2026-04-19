<template>
  <div class="chat-page">

    <!-- 顶部微弱日出光晕（与首页呼应） -->
    <div class="ambient-bg" aria-hidden="true">
      <div class="glow-a"></div>
      <div class="glow-b"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="top-bar">
      <div class="top-bar-left">
        <div class="brand-icon">
          <img src="/logo.png" width="25" height="25" alt="政策通" />
        </div>
        <span class="brand-name">政策通</span>
        <span class="divider">/</span>
        <div class="mode-badge">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
          </svg>
          政策问答
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

    <!-- 消息列表区域 -->
    <div class="messages-scroll" ref="scrollContainer" @scroll="handleScroll">
      <div class="messages-inner">

        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
            </svg>
          </div>
          <p class="empty-title">政策问答</p>
          <p class="empty-subtitle">快速回答你的政策问题</p>
        </div>

        <!-- 消息列表 -->
        <div v-for="(msg, index) in messages" :key="index" class="message-row">

          <!-- 用户消息 -->
          <div v-if="msg.isUser" class="row-user">
            <div class="user-bubble">{{ msg.content }}</div>
            <div class="user-avatar">
              <img src="@/assets/chat-user.png" width="15" height="15" alt="用户" />
            </div>
          </div>

          <!-- AI 消息 -->
          <div v-else class="row-ai group">
            <div class="ai-avatar">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
              </svg>
            </div>
            <div class="ai-body">
              <!-- 有内容：渲染 Markdown -->
              <div class="ai-bubble" v-if="msg.content">
                <div class="markdown-body" v-html="renderMarkdown(msg.content)"></div>
                <span
                  v-if="connectionStatus === 'connecting' && index === messages.length - 1"
                  class="typing-cursor"
                ></span>
              </div>
              <!-- 无内容+正在生成：三点加载 -->
              <div class="ai-loading" v-else-if="connectionStatus === 'connecting' && index === messages.length - 1">
                <div class="dot"></div>
                <div class="dot" style="animation-delay:0.2s"></div>
                <div class="dot" style="animation-delay:0.4s"></div>
              </div>
              <!-- 复制按钮（hover 显示） -->
              <div
                class="action-bar"
                v-if="msg.content && !(connectionStatus === 'connecting' && index === messages.length - 1)"
              >
                <button class="action-btn" @click="copyMessage(msg.content, index)">
                  <svg v-if="copiedIndex !== index" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                  </svg>
                  <svg v-else width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-green">
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
      <img src="@/assets/home-send.png" width="18" height="18" alt="发送" />
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
            placeholder="输入你的政策问题…"
            :disabled="connectionStatus === 'connecting'"
            rows="1"
            class="input-textarea"
          ></textarea>

          <!-- 底部工具栏 -->
          <div class="input-footer">
            <div class="hint-text">
              <template v-if="connectionStatus === 'connecting'">
                <span class="generating">
                  <svg class="spin" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                  </svg>
                  正在生成…
                </span>
              </template>
              <template v-else>Enter 发送 · Shift+Enter 换行</template>
            </div>

            <!-- 生成中：停止按钮 -->
            <button
              v-if="connectionStatus === 'connecting'"
              class="stop-btn"
              @click="stopGenerate"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                <rect x="6" y="6" width="12" height="12" rx="2"/>
              </svg>
            </button>

            <!-- 空闲：发送按钮 -->
            <button
              v-else
              class="send-btn"
              :class="{ 'send-active': inputMessage.trim() }"
              @click="sendMessage"
              :disabled="!inputMessage.trim()"
            >
              <img src="@/assets/chat-send.png" width="18" height="18" alt="发送" />
            </button>
          </div>
        </div>

        <!-- 重新生成 -->
        <div class="regen-bar" v-if="messages.length > 0 && connectionStatus !== 'connecting'">
          <button class="regen-btn" @click="regenerate">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="23 4 23 10 17 10"/>
              <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
            </svg>
            重新生成
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import { marked } from 'marked'
import { chatWithPolicyApp, getPersistentChatId } from '../api'

// ── 配置 marked ────────────────────────────────────────────
marked.setOptions({
  breaks: true,
  gfm: true,
})

useHead({
  title: '政策问答 - 北京政策智能助手平台',
  meta: [
    { name: 'description', content: '基于RAG知识库的北京政策问答助手，快速解答社保、交通、积分落户等政策问题' },
    { name: 'keywords', content: '北京政策,社保查询,进京证,积分落户,政策问答,RAG知识库' }
  ]
})

const router = useRouter()

// ── 状态 ──────────────────────────────────────────────────
const messages        = ref([])
const chatId          = ref('')
const connectionStatus = ref('disconnected')
const inputMessage    = ref('')
const isInputFocused  = ref(false)
const copiedIndex     = ref(-1)
const showScrollDown  = ref(false)
const selectedModel   = ref('') // 从首页传过来的模型选择

const scrollContainer = ref(null)
const messagesEnd     = ref(null)
const textareaRef     = ref(null)

let eventSource = null

// ── 消息管理 ──────────────────────────────────────────────
const addMessage = (content, isUser) => {
  messages.value.push({ content, isUser, time: Date.now() })
}

// ── 发送消息（SSE 调用，保留原有逻辑）────────────────────
const sendMessage = () => {
  if (!inputMessage.value.trim() || connectionStatus.value === 'connecting') return

  const text = inputMessage.value.trim()
  addMessage(text, true)
  inputMessage.value = ''

  nextTick(() => {
    if (textareaRef.value) textareaRef.value.style.height = 'auto'
  })

  if (eventSource) eventSource.close()

  const aiMessageIndex = messages.value.length
  addMessage('', false)

  connectionStatus.value = 'connecting'
  eventSource = chatWithPolicyApp(text, chatId.value, selectedModel.value || undefined)

  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content += data
      }
    }
    if (data === '[DONE]') {
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }

  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }

  nextTick(() => scrollToBottom())
}

// ── 停止生成 ──────────────────────────────────────────────
const stopGenerate = () => {
  if (eventSource) eventSource.close()
  connectionStatus.value = 'disconnected'
}

// ── 重新生成 ──────────────────────────────────────────────
const regenerate = () => {
  if (connectionStatus.value === 'connecting') return
  const lastUserMsg = [...messages.value].reverse().find(m => m.isUser)
  if (!lastUserMsg) return
  const lastIndex = messages.value.length - 1
  if (!messages.value[lastIndex].isUser) messages.value.splice(lastIndex, 1)
  inputMessage.value = lastUserMsg.content
  sendMessage()
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
  sessionStorage.removeItem('chatId')
  chatId.value = getPersistentChatId()
  addMessage('您好！我是北京政策问答助手，可以帮您解答社保、交通、积分落户等政策问题。请问有什么可以帮您？', false)
}

// ── Markdown 预处理（修复 AI 输出的常见格式问题）─────────
const preprocessMarkdown = (text) => {
  if (!text) return ''
  let md = text

  // 修复错位的粗体标记：*文本** → **文本**
  // AI 常输出 *方式1：通过"北京交警"APP**，实际意图是 **方式1：通过"北京交警"APP**
  md = md.replace(/^\*([^*\n]+\S)\*\*\s*$/gm, '**$1**')

  // 修复行首缺少空格的列表标记（中英文混排常见）
  md = md.replace(/^([-*])\s*(?=[^\s])/gm, '$1 ')
  md = md.replace(/^(\d+\.)\s*(?=[^\s\d])/gm, '$1 ')

  // 修复行首缺少空格的标题标记（###标题 → ### 标题）
  md = md.replace(/^(#{1,6})\s*(?=[^\s#])/gm, '$1 ')

  // 修复引用块缺少空格（>text → > text）
  md = md.replace(/^>\s*(?=[^\s>])/gm, '> ')

  return md
}

// ── Markdown 渲染 ─────────────────────────────────────────
const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    return marked.parse(preprocessMarkdown(text))
  } catch {
    // marked 解析失败时做基本转义
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\n/g, '<br/>')
  }
}

// ── 生命周期 ──────────────────────────────────────────────
onMounted(() => {
  chatId.value = getPersistentChatId()

  // 读取首页带过来的模型选择
  const initModel = sessionStorage.getItem('initModel')
  if (initModel) {
    selectedModel.value = initModel
    sessionStorage.removeItem('initModel')
  }

  // 读取首页带过来的初始问题
  const initQuestion = sessionStorage.getItem('initQuestion')
  sessionStorage.removeItem('initQuestion')

  if (initQuestion && initQuestion.trim()) {
    // 有初始问题：直接发送，不显示欢迎语
    inputMessage.value = initQuestion.trim()
    nextTick(() => sendMessage())
  } else {
    addMessage('您好！我是北京政策问答助手，可以帮您解答社保、交通、积分落户等政策问题。请问有什么可以帮您？', false)
  }
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

/* ── 日出光晕（与首页呼应，极淡） ── */
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
    rgba(220,38,38,0.07) 0%, rgba(249,115,22,0.05) 40%, transparent 70%);
  filter: blur(40px);
}
.glow-b {
  position: absolute;
  top: 0; left: 0;
  width: 320px; height: 320px;
  background: radial-gradient(circle at 10% 10%,
    rgba(251,191,36,0.06) 0%, transparent 65%);
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-name { font-size: 14px; font-weight: 500; color: #334155; }
.divider    { font-size: 14px; color: #cbd5e1; }

.mode-badge {
  display: flex; align-items: center; gap: 6px;
  padding: 4px 12px; border-radius: 100px;
  background: #fffbeb;
  border: 1px solid #fde68a;
  color: #b45309; font-size: 12px; font-weight: 500;
}

.top-bar-right { display: flex; align-items: center; gap: 4px; }

.nav-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 12px; border-radius: 8px;
  background: transparent; border: none;
  color: #64748b; font-size: 13px; cursor: pointer;
  transition: all 0.15s;
}
.nav-btn:hover { background: #f1f5f9; color: #0f172a; }

/* ── 消息滚动区 ── */
.messages-scroll {
  position: relative;
  z-index: 5;
  flex: 1; overflow-y: auto; overflow-x: hidden;
  background: linear-gradient(to bottom, rgba(255,248,246,0.4) 0%, rgba(255,255,255,0) 200px);
}
.messages-scroll::-webkit-scrollbar       { width: 4px; }
.messages-scroll::-webkit-scrollbar-track { background: transparent; }
.messages-scroll::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }

.messages-inner {
  max-width: 768px;
  margin: 0 auto;
  padding: 32px 20px;
}

/* ── 空状态 ── */
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; height: 256px; text-align: center;
  user-select: none;
}
.empty-icon {
  width: 56px; height: 56px; border-radius: 18px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  display: flex; align-items: center; justify-content: center;
  color: white; margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.2);
}
.empty-title   { font-size: 16px; font-weight: 600; color: #0f172a; margin: 0 0 4px; }
.empty-subtitle{ font-size: 13px; color: #94a3b8; margin: 0; }

/* ── 消息行 ── */
.message-row { margin-bottom: 24px; animation: msgIn 0.25s ease-out; }

@keyframes msgIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* 用户消息 */
.row-user {
  display: flex; justify-content: flex-end;
  align-items: flex-end; gap: 10px;
}

.user-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 18px; border-bottom-right-radius: 4px;
  background: #0f172a; color: #fff;
  font-size: 0.9rem; line-height: 1.65;
  word-break: break-word;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.user-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: #e2e8f0;
  display: flex; align-items: center; justify-content: center;
  color: #64748b; flex-shrink: 0;
}

/* AI 消息 */
.row-ai {
  display: flex; justify-content: flex-start;
  align-items: flex-start; gap: 12px;
}

.ai-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, #ef4444, #f97316);
  display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0; margin-top: 4px;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}

.ai-body { flex: 1; min-width: 0; max-width: 86%; }

.ai-bubble {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 18px; border-top-left-radius: 4px;
  padding: 16px 20px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05);
  color: #334155; font-size: 0.87rem; line-height: 1.7;
}

/* 光标 */
.typing-cursor {
  display: inline-block; width: 2px; height: 16px;
  background: #ef4444; margin-left: 2px;
  vertical-align: middle; border-radius: 1px;
  animation: blink 1s step-end infinite;
}
@keyframes blink { 0%,100%{opacity:1} 50%{opacity:0} }

/* 加载三点 */
.ai-loading {
  display: flex; align-items: center; gap: 6px;
  padding: 16px 20px;
  background: #fff; border: 1px solid #e2e8f0;
  border-radius: 18px; border-top-left-radius: 4px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05);
}
.dot {
  width: 6px; height: 6px; border-radius: 50%; background: #cbd5e1;
  animation: dotPulse 1.4s ease-in-out infinite;
}
@keyframes dotPulse { 0%,100%{opacity:0.4;transform:scale(0.8)} 50%{opacity:1;transform:scale(1)} }

/* 复制操作栏 */
.action-bar {
  display: flex; gap: 4px; margin-top: 8px;
  opacity: 0; transition: opacity 0.15s;
}
.row-ai:hover .action-bar { opacity: 1; }

.action-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 4px 8px; border-radius: 6px;
  background: transparent; border: none;
  color: #94a3b8; font-size: 12px; cursor: pointer;
  transition: all 0.15s;
}
.action-btn:hover { background: #f1f5f9; color: #475569; }
.icon-green { color: #16a34a; }

/* ── 滚动到底部 ── */
.scroll-down-btn {
  position: fixed; bottom: 112px; right: 24px;
  width: 36px; height: 36px; border-radius: 50%;
  background: #fff; border: 1px solid #e2e8f0;
  color: #64748b; cursor: pointer; z-index: 20;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: all 0.15s;
}
.scroll-down-btn:hover { background: #f8fafc; color: #0f172a; box-shadow: 0 6px 18px rgba(0,0,0,0.12); }

/* ── 输入区域 ── */
.input-area {
  position: relative;
  z-index: 10;
  flex-shrink: 0;
  padding: 12px 20px 24px;
  background: #fff;
  border-top: 1px solid #f1f5f9;
}

.input-container { max-width: 768px; margin: 0 auto; }

.input-box {
  position: relative;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.input-box.focused {
  border-color: #fca5a5;
  box-shadow: 0 0 0 3px rgba(239,68,68,0.08), 0 8px 24px rgba(239,68,68,0.1);
}

.input-textarea {
  width: 100%; background: transparent;
  border: none; outline: none; resize: none;
  color: #0f172a; font-size: 0.9rem; line-height: 1.65;
  padding: 16px 20px 52px;
  font-family: inherit; max-height: 160px;
  overflow-y: auto; box-sizing: border-box;
}
.input-textarea::placeholder { color: #94a3b8; }
.input-textarea:disabled { opacity: 0.5; }
.input-textarea::-webkit-scrollbar { width: 4px; }
.input-textarea::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }

/* 底部工具栏 */
.input-footer {
  position: absolute; bottom: 10px; left: 16px; right: 16px;
  display: flex; align-items: center; justify-content: space-between;
}

.hint-text { font-size: 12px; color: #94a3b8; }

.generating {
  display: flex; align-items: center; gap: 6px;
  color: #ef4444;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* 停止按钮 */
.stop-btn {
  width: 32px; height: 32px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #ef4444; cursor: pointer;
  transition: all 0.15s;
}
.stop-btn:hover { background: #fee2e2; }

/* 发送按钮 */
.send-btn {
  width: 32px; height: 32px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: #f1f5f9; border: none; color: #cbd5e1;
  cursor: not-allowed; transition: all 0.15s;
}
.send-btn.send-active {
  background: #F55D2D;
  color: #fff;
  cursor: pointer;
}

/* 有输入时图标变白色（亮色） */
.send-btn.send-active img {
  filter: brightness(0) invert(1);
}

.send-btn.send-active:hover {
  background: #F55D2D;
}
/* 重新生成 */
.regen-bar { display: flex; justify-content: center; margin-top: 10px; }
.regen-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 14px; border-radius: 100px;
  background: transparent; border: none;
  color: #94a3b8; font-size: 12px; cursor: pointer;
  transition: all 0.15s;
}
.regen-btn:hover { background: #f1f5f9; color: #475569; }

/* ── Markdown 渲染（浅色，基于 marked 标准输出） ── */
.markdown-body :deep(h1) {
  font-size: 1.15rem; font-weight: 700; color: #0f172a;
  margin: 16px 0 8px;
}
.markdown-body :deep(h2) {
  font-size: 1rem; font-weight: 600; color: #1e293b;
  margin: 20px 0 8px; padding-bottom: 6px;
  border-bottom: 1px solid #e2e8f0;
}
.markdown-body :deep(h3) {
  font-size: 0.9rem; font-weight: 600; color: #334155;
  margin: 12px 0 6px;
}
.markdown-body :deep(h4) {
  font-size: 0.87rem; font-weight: 600; color: #475569;
  margin: 10px 0 4px;
}
.markdown-body :deep(p) {
  margin: 4px 0; color: #475569;
  font-size: 0.87rem; line-height: 1.7;
}
.markdown-body :deep(strong) { color: #0f172a; font-weight: 600; }
.markdown-body :deep(em) { font-style: italic; color: #475569; }
.markdown-body :deep(code) {
  padding: 2px 6px; border-radius: 4px;
  background: #fef2f2; color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 0.82rem; font-family: 'Fira Code', Consolas, monospace;
}
.markdown-body :deep(pre) {
  margin: 10px 0; padding: 16px; border-radius: 10px;
  background: #f8fafc; border: 1px solid #e2e8f0;
  overflow-x: auto; line-height: 1.6;
  font-size: 0.8rem; font-family: 'Fira Code', Consolas, monospace;
}
.markdown-body :deep(pre code) {
  padding: 0; border: none; background: none; color: #334155;
  font-size: inherit;
}
.markdown-body :deep(blockquote) {
  margin: 10px 0; padding: 12px 16px;
  border-left: 3px solid #f59e0b;
  background: rgba(251,191,36,0.06);
  border-radius: 0 10px 10px 0;
  color: #64748b; font-size: 0.85rem;
}
.markdown-body :deep(blockquote p) { color: inherit; font-size: inherit; }
.markdown-body :deep(ul),
.markdown-body :deep(ol) { margin: 6px 0; padding-left: 20px; }
.markdown-body :deep(li) { color: #475569; font-size: 0.87rem; line-height: 1.7; margin-bottom: 4px; }
.markdown-body :deep(li input[type="checkbox"]) { margin-right: 6px; }
.markdown-body :deep(a) {
  color: #ef4444; text-decoration: none;
  border-bottom: 1px solid rgba(239,68,68,0.3);
  transition: border-color 0.15s, color 0.15s;
}
.markdown-body :deep(a:hover) {
  color: #dc2626; border-bottom-color: #dc2626;
}
.markdown-body :deep(hr) {
  border: none; border-top: 1px solid #e2e8f0; margin: 16px 0;
}
.markdown-body :deep(table) {
  width: 100%; border-collapse: collapse; font-size: 0.82rem;
  margin: 12px 0; border-radius: 10px; border: 1px solid #e2e8f0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  overflow: hidden;
}
.markdown-body :deep(thead) { background: #f8fafc; }
.markdown-body :deep(th) {
  padding: 10px 16px; text-align: left;
  color: #475569; font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}
.markdown-body :deep(td) {
  padding: 9px 16px; color: #64748b;
  border-bottom: 1px solid #f1f5f9;
}
.markdown-body :deep(tr:last-child td) { border-bottom: none; }
.markdown-body :deep(tbody tr:hover td) { background: rgba(239,68,68,0.025); }

/* ── 响应式 ── */
@media (max-width: 768px) {
  .top-bar        { padding: 10px 12px; }
  .messages-inner { padding: 20px 12px; }
  .input-area     { padding: 10px 12px 20px; }
  .user-bubble    { max-width: 85%; }
  .ai-body        { max-width: 90%; }
  .brand-name, .divider { display: none; }
}

@media (max-width: 480px) {
  .top-bar        { padding: 8px 10px; }
  .messages-inner { padding: 16px 10px; }
  .input-area     { padding: 8px 10px 16px; }
}
</style>
