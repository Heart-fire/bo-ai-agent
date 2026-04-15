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
          <img src="/政策通知.png" width="25" height="25" alt="政策通" />
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
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
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
              <img src="@/assets/025-发送.png" width="18" height="18" alt="发送" />
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
import { chatWithPolicyApp, getPersistentChatId } from '../api'

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
  eventSource = chatWithPolicyApp(text, chatId.value)

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
  localStorage.removeItem('chatId')
  chatId.value = getPersistentChatId()
  addMessage('您好！我是北京政策问答助手，可以帮您解答社保、交通、积分落户等政策问题。请问有什么可以帮您？', false)
}

// ── Markdown 渲染 ─────────────────────────────────────────
const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // 代码块
  html = html.replace(/```[\s\S]*?```/g, (match) => {
    const code = match.replace(/^```[^\n]*\n?/, '').replace(/```$/, '')
    return `<pre class="md-code"><code>${code}</code></pre>`
  })

  // 表格
  html = html.replace(/(\|.+\|[\r\n]+\|[-| :]+\|[\r\n]+((\|.+\|[\r\n]*)+))/g, (match) => {
    const lines = match.trim().split('\n')
    const headers = lines[0].split('|').filter(s => s.trim()).map(h => `<th>${h.trim()}</th>`).join('')
    const rows = lines.slice(2).map(row => {
      const cells = row.split('|').filter(s => s.trim()).map(c => `<td>${c.trim()}</td>`).join('')
      return `<tr>${cells}</tr>`
    }).join('')
    return `<div class="md-table-wrap"><table class="md-table"><thead><tr>${headers}</tr></thead><tbody>${rows}</tbody></table></div>`
  })

  // 水平线
  html = html.replace(/^---$/gm, '<hr class="md-hr"/>')

  // 引用块
  html = html.replace(/^&gt; (.+)$/gm, '<blockquote class="md-blockquote">$1</blockquote>')

  // 标题
  html = html.replace(/^### (.+)$/gm, '<h4 class="md-h3">$1</h4>')
  html = html.replace(/^## (.+)$/gm,  '<h3 class="md-h2">$1</h3>')
  html = html.replace(/^# (.+)$/gm,   '<h2 class="md-h1">$1</h2>')

  // Checklist
  html = html.replace(/^- \[x\] (.+)$/gm, '<li class="md-check checked">✓ $1</li>')
  html = html.replace(/^- \[ \] (.+)$/gm, '<li class="md-check">○ $1</li>')

  // 无序列表
  html = html.replace(/^[-*•] (.+)$/gm, '<li class="md-li">$1</li>')
  html = html.replace(/(<li class="md-li">.*<\/li>\n?)+/g, m => `<ul class="md-ul">${m}</ul>`)

  // 有序列表
  html = html.replace(/^\d+\. (.+)$/gm, '<li class="md-oli">$1</li>')
  html = html.replace(/(<li class="md-oli">.*<\/li>\n?)+/g, m => `<ol class="md-ol">${m}</ol>`)

  // 粗体 & 内联代码
  html = html.replace(/\*\*([^*]+)\*\*/g, '<strong class="md-bold">$1</strong>')
  html = html.replace(/`([^`]+)`/g, '<code class="md-inline-code">$1</code>')

  // 段落
  html = html.replace(/\n\n/g, '</p><p class="md-p">')
  html = html.replace(/\n/g, '<br/>')
  html = `<p class="md-p">${html}</p>`

  html = html.replace(/<p class="md-p"><(h[1-4]|ul|ol|pre|blockquote|hr|div)/g, '<$1')
  html = html.replace(/<\/(h[1-4]|ul|ol|pre|blockquote|div)><\/p>/g, '</$1>')

  return html
}

// ── 生命周期 ──────────────────────────────────────────────
onMounted(() => {
  chatId.value = getPersistentChatId()

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
  background: #1296db;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(18, 150, 219, 0.35);
}

/* 有输入时图标变白色（亮色） */
.send-btn.send-active img {
  filter: brightness(0) invert(1);
}

.send-btn.send-active:hover {
  background: #0ea5e9;
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

/* ── Markdown 渲染（浅色） ── */
.markdown-body :deep(.md-h1) {
  font-size: 1.15rem; font-weight: 700; color: #0f172a;
  margin: 16px 0 8px;
}
.markdown-body :deep(.md-h2) {
  font-size: 1rem; font-weight: 600; color: #1e293b;
  margin: 20px 0 8px; padding-bottom: 6px;
  border-bottom: 1px solid #e2e8f0;
}
.markdown-body :deep(.md-h3) {
  font-size: 0.9rem; font-weight: 600; color: #334155;
  margin: 12px 0 6px;
}
.markdown-body :deep(.md-p) {
  margin: 4px 0; color: #475569;
  font-size: 0.87rem; line-height: 1.7;
}
.markdown-body :deep(.md-bold) { color: #0f172a; font-weight: 600; }
.markdown-body :deep(.md-inline-code) {
  padding: 2px 6px; border-radius: 4px;
  background: #fef2f2; color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 0.82rem; font-family: 'Fira Code', Consolas, monospace;
}
.markdown-body :deep(.md-code) {
  margin: 10px 0; padding: 16px; border-radius: 10px;
  background: #f8fafc; border: 1px solid #e2e8f0;
  color: #334155; font-size: 0.8rem; overflow-x: auto; line-height: 1.6;
  font-family: 'Fira Code', Consolas, monospace;
}
.markdown-body :deep(.md-blockquote) {
  margin: 10px 0; padding: 12px 16px;
  border-left: 3px solid #f59e0b;
  background: rgba(251,191,36,0.06);
  border-radius: 0 10px 10px 0;
  color: #64748b; font-size: 0.85rem;
}
.markdown-body :deep(.md-ul),
.markdown-body :deep(.md-ol)  { margin: 6px 0; padding-left: 20px; }
.markdown-body :deep(.md-li),
.markdown-body :deep(.md-oli) { color: #475569; font-size: 0.87rem; line-height: 1.7; margin-bottom: 4px; }
.markdown-body :deep(.md-check) {
  list-style: none; color: #94a3b8;
  font-size: 0.87rem; margin-left: -20px; padding-left: 4px; margin-bottom: 4px;
}
.markdown-body :deep(.md-check.checked) { color: #16a34a; }
.markdown-body :deep(.md-hr) {
  border: none; border-top: 1px solid #e2e8f0; margin: 16px 0;
}
.markdown-body :deep(.md-table-wrap) {
  margin: 12px 0; overflow-x: auto;
  border-radius: 10px; border: 1px solid #e2e8f0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.markdown-body :deep(.md-table) { width: 100%; border-collapse: collapse; font-size: 0.82rem; }
.markdown-body :deep(.md-table th) {
  padding: 10px 16px; text-align: left;
  background: #f8fafc;
  color: #475569; font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}
.markdown-body :deep(.md-table td) {
  padding: 9px 16px; color: #64748b;
  border-bottom: 1px solid #f1f5f9;
}
.markdown-body :deep(.md-table tr:last-child td) { border-bottom: none; }
.markdown-body :deep(.md-table tr:hover td) { background: rgba(239,68,68,0.025); }

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
