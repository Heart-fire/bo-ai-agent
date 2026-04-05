<template>
  <div class="super-agent-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="glow-orb glow-1"></div>
      <div class="glow-orb glow-2"></div>
      <div class="glow-orb glow-3"></div>
      <div class="gradient-mesh"></div>
    </div>

    <!-- 毛玻璃头部 -->
    <header class="glass-header">
      <div class="header-content">
        <button class="back-button" @click="goBack">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
        <div class="header-center">
          <div class="icon-wrapper">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2M7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16a1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 7.5 13m9 0a1.5 1.5 0 0 0-1.5 1.5 1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5 1.5 1.5 0 0 0-1.5-1.5M12 9a5 5 0 0 0-5 5v1h10v-1a5 5 0 0 0-5-5z"/>
            </svg>
          </div>
          <div>
            <h1 class="title">AI超级智能体</h1>
            <p class="subtitle">全能型AI助手 · 专业问题解答</p>
          </div>
        </div>
        <div class="header-spacer"></div>
      </div>
    </header>

    <div class="content-wrapper">
      <!-- 特性展示区域 -->
      <div class="features-section">
        <div class="features-container">
          <!-- 主标题区 -->
          <div class="hero-section">
            <div class="hero-badge">全新升级</div>
            <h1 class="hero-title">AI 超级智能体</h1>
            <p class="hero-subtitle">从对话助手，到真正能工作的 AI Agent</p>
          </div>

          <!-- 能力卡片网格 -->
          <div class="capabilities-grid">
            <!-- 卡片1: 多步骤推理 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-blue">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <h3 class="card-title">多步骤推理</h3>
              <p class="card-description">模拟人类思维链，逐步分解复杂问题，提供清晰的推导过程和可靠的答案</p>
              <div class="card-example">
                <span class="example-label">示例：</span>
                <span class="example-text">分析市场趋势 → 评估竞争 → 制定策略</span>
              </div>
            </div>

            <!-- 卡片2: 工具调用 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-purple">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 00-1-1H4a2 2 0 110-4h1a1 1 0 001-1V7a1 1 0 011-1h3a1 1 0 001-1V4z"/>
                </svg>
              </div>
              <h3 class="card-title">工具调用</h3>
              <p class="card-description">智能选择并调用外部工具，自动执行搜索、计算、数据处理等实际操作</p>
              <div class="card-example">
                <span class="example-label">支持：</span>
                <span class="example-text">网页搜索 · 数据查询 · API调用</span>
              </div>
            </div>

            <!-- 卡片3: MCP 服务 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-cyan">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"/>
                </svg>
              </div>
              <h3 class="card-title">MCP 服务</h3>
              <p class="card-description">通过 MCP 协议接入更多能力与系统，支持自定义扩展，打造专属智能体</p>
              <div class="card-example">
                <span class="example-label">扩展：</span>
                <span class="example-text">图片搜索 · 文件处理 · 系统集成</span>
              </div>
            </div>

            <!-- 卡片4: 自动任务执行 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-orange">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
              <h3 class="card-title">自动任务执行</h3>
              <p class="card-description">AI 可以完成完整任务流程，自动规划、执行、验证，实现真正的自动化工作流</p>
              <div class="card-example">
                <span class="example-label">场景：</span>
                <span class="example-text">数据分析 · 报告生成 · 流程自动化</span>
              </div>
            </div>
          </div>

          <!-- 推理流程示例 -->
          <div class="reasoning-demo">
            <div class="demo-header">
              <h2 class="demo-title">智能推理流程</h2>
              <p class="demo-subtitle">看看 AI 是如何思考的</p>
            </div>
            <div class="demo-steps">
              <div class="demo-step">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h4 class="step-title">理解任务</h4>
                  <p class="step-text">分析用户需求，明确目标和约束条件</p>
                </div>
                <div class="step-arrow">→</div>
              </div>
              <div class="demo-step">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h4 class="step-title">制定计划</h4>
                  <p class="step-text">分解任务，确定执行步骤和所需工具</p>
                </div>
                <div class="step-arrow">→</div>
              </div>
              <div class="demo-step">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h4 class="step-title">执行验证</h4>
                  <p class="step-text">按步骤执行，实时验证并调整策略</p>
                </div>
                <div class="step-arrow">→</div>
              </div>
              <div class="demo-step">
                <div class="step-number">4</div>
                <div class="step-content">
                  <h4 class="step-title">输出结果</h4>
                  <p class="step-text">汇总结论，提供详细分析和建议</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部CTA -->
          <div class="cta-section">
            <div class="cta-content">
              <h2 class="cta-title">开始体验 AI 超级智能体</h2>
              <p class="cta-subtitle">在下方输入框提出你的问题，让 AI 为你完成复杂任务</p>
            </div>
            <div class="cta-indicator">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 14l-7 7m0 0l-7-7m7 7V3"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-area">
        <ChatRoom
          :messages="messages"
          :connection-status="connectionStatus"
          ai-type="super"
          @send-message="sendMessage"
        />
      </div>
    </div>

    <div class="footer-container">
      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ChatRoom from '../components/ChatRoom.vue'
import AppFooter from '../components/AppFooter.vue'
import { chatWithResearchAgent as chatWithManus } from '../api'
import { parseMessage, MessageType } from '../types/structured-message'

// 设置页面标题和元数据
useHead({
  title: 'AI超级智能体 - AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI超级智能体是AI超级智能体应用平台的全能助手，能解答各类专业问题，提供精准建议和解决方案'
    },
    {
      name: 'keywords',
      content: 'AI超级智能体,智能助手,专业问答,AI问答,专业建议,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const connectionStatus = ref('disconnected')
let eventSource = null

// Agent response state
let agentResponseIndex = -1
let requestStartTime = 0

// 添加消息到列表
const addMessage = (content, isUser, type = '', isStructured = false) => {
  messages.value.push({
    content,
    isUser,
    type,
    isStructured,
    time: new Date().getTime()
  })
}

// Create or get the agent response message
const ensureAgentResponse = () => {
  if (agentResponseIndex < 0 || agentResponseIndex >= messages.value.length) {
    messages.value.push({
      content: {
        steps: [],
        toolResults: [],
        summary: null,
        isDone: false,
        elapsedTime: 0
      },
      isUser: false,
      type: 'agent-response',
      isStructured: false,
      time: new Date().getTime()
    })
    agentResponseIndex = messages.value.length - 1
  }
  return messages.value[agentResponseIndex].content
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true, 'user-question')
  agentResponseIndex = -1
  requestStartTime = Date.now()

  if (eventSource) {
    eventSource.close()
  }

  connectionStatus.value = 'connecting'

  // Temporary buffer for plain text
  let messageBuffer = []

  eventSource = chatWithManus(message)

  // 监听SSE消息
  eventSource.onmessage = (event) => {
    const data = event.data

    if (data === '[DONE]') {
      // Flush remaining text if no agent data collected
      if (messageBuffer.length > 0 && agentResponseIndex < 0) {
        addMessage(messageBuffer.join(''), false, 'ai-final')
        messageBuffer = []
      }
      // Mark agent as done if exists
      if (agentResponseIndex >= 0) {
        const agentContent = ensureAgentResponse()
        agentContent.isDone = true
        agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      }
      connectionStatus.value = 'disconnected'
      eventSource.close()
      return
    }

    const parsed = parseMessage(data)

    if (parsed === null) {
      return
    }

    if (typeof parsed === 'object' && parsed.messageType) {
      const agentContent = ensureAgentResponse()

      if (parsed.messageType === MessageType.STEP && parsed.data) {
        agentContent.steps.push(parsed.data)
      } else if (parsed.messageType === MessageType.TOOL_RESULT && parsed.data) {
        agentContent.toolResults.push(...parsed.data)
      } else if (parsed.messageType === MessageType.SUMMARY && parsed.data) {
        agentContent.summary = parsed.data
        agentContent.isDone = true
        agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      } else if (parsed.messageType === MessageType.ERROR && parsed.data) {
        agentContent.error = parsed.data
        agentContent.isDone = true
        agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
      }

      if (parsed.messageType === MessageType.ERROR) {
        connectionStatus.value = 'error'
      } else if (parsed.messageType === MessageType.SUMMARY) {
        connectionStatus.value = 'disconnected'
      } else {
        connectionStatus.value = 'connected'
      }
    } else {
      // Plain text: buffer for fallback display
      messageBuffer.push(data)
    }
  }

  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    if (agentResponseIndex >= 0) {
      const agentContent = ensureAgentResponse()
      agentContent.isDone = true
      agentContent.elapsedTime = Math.round((Date.now() - requestStartTime) / 1000)
    }
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  addMessage('你好，我是AI超级智能体。我可以解答各类问题，提供专业建议，请问有什么可以帮助你的吗？', false)
})

onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
/* ============================================
   CSS 变量系统
   ============================================ */
.super-agent-container {
  --primary-blue: #2b6fff;
  --primary-cyan: #06b6d4;
  --primary-blue-light: #5b8fff;
  --text-primary: #1a1a1a;
  --text-secondary: #6b7280;
  --glass-bg: hsla(0, 0%, 100%, 0.75);
  --glass-border: rgba(255, 255, 255, 0.8);
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.04);
  --shadow-md: 0 4px 12px 0 rgb(0 0 0 / 0.06);
  --shadow-lg: 0 8px 24px 0 rgb(0 0 0 / 0.08);

  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #f9fbff 0%, #e8f0ff 50%, #f0f9ff 100%);
  position: relative;
  overflow-x: hidden;
}

/* ============================================
   背景装饰
   ============================================ */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: orbFloat 20s ease-in-out infinite;
}

.glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(43, 111, 255, 0.35) 0%, transparent 70%);
  top: -150px;
  right: -150px;
  animation-delay: 0s;
}

.glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.3) 0%, transparent 70%);
  bottom: -100px;
  left: -100px;
  animation-delay: -7s;
}

.glow-3 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(91, 143, 255, 0.25) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

.gradient-mesh {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(ellipse 80% 50% at 20% 40%, rgba(43, 111, 255, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse 60% 40% at 80% 60%, rgba(6, 182, 212, 0.06) 0%, transparent 50%);
  opacity: 0.8;
}

/* ============================================
   毛玻璃头部
   ============================================ */
.glass-header {
  position: sticky;
  top: 16px;
  z-index: 100;
  margin: 0 16px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: var(--shadow-md);
}

.back-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-button:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--text-primary);
}

.back-button:active {
  transform: scale(0.96);
}

.back-button svg {
  transition: transform 0.2s ease;
}

.back-button:hover svg {
  transform: translateX(-2px);
}

.header-center {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: center;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-cyan) 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(43, 111, 255, 0.3);
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.2;
}

.subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 2px 0 0 0;
}

.header-spacer {
  width: 74px;
  flex-shrink: 0;
}

/* ============================================
   内容区
   ============================================ */
.content-wrapper {
  flex: 1;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
}

/* ============================================
   特性展示区域
   ============================================ */
.features-section {
  position: relative;
  z-index: 1;
  padding: 20px 16px;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 主标题区 */
.hero-section {
  text-align: center;
  margin-bottom: 48px;
  animation: fadeInUp 0.6s ease-out;
}

.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, rgba(43, 111, 255, 0.1) 0%, rgba(6, 182, 212, 0.1) 100%);
  border: 1px solid rgba(43, 111, 255, 0.2);
  border-radius: 20px;
  color: var(--primary-blue);
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: 42px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-cyan) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 400;
}

/* 能力卡片网格 */
.capabilities-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 48px;
}

.capability-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 28px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-md);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out backwards;
}

.capability-card:nth-child(1) { animation-delay: 0.1s; }
.capability-card:nth-child(2) { animation-delay: 0.2s; }
.capability-card:nth-child(3) { animation-delay: 0.3s; }
.capability-card:nth-child(4) { animation-delay: 0.4s; }

.capability-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
}

.card-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.card-icon-blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.card-icon-purple {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
}

.card-icon-cyan {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  box-shadow: 0 8px 20px rgba(6, 182, 212, 0.3);
}

.card-icon-orange {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  box-shadow: 0 8px 20px rgba(249, 115, 22, 0.3);
}

.card-icon {
  width: 28px;
  height: 28px;
  color: white;
  stroke-width: 2;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px 0;
}

.card-description {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin: 0 0 16px 0;
}

.card-example {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.example-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-blue);
}

.example-text {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 推理流程演示 */
.reasoning-demo {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.5) 100%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 32px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  margin-bottom: 40px;
  animation: fadeInUp 0.6s ease-out 0.5s backwards;
}

.demo-header {
  text-align: center;
  margin-bottom: 32px;
}

.demo-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.demo-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.demo-steps {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.demo-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-cyan) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(43, 111, 255, 0.3);
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.step-text {
  font-size: 13px;
  line-height: 1.5;
  color: var(--text-secondary);
  margin: 0;
}

.step-arrow {
  position: absolute;
  right: -12px;
  top: 50px;
  font-size: 20px;
  color: rgba(43, 111, 255, 0.3);
}

.demo-step:last-child .step-arrow {
  display: none;
}

/* CTA区域 */
.cta-section {
  background: linear-gradient(135deg, rgba(43, 111, 255, 0.08) 0%, rgba(6, 182, 212, 0.08) 100%);
  border: 1px solid rgba(43, 111, 255, 0.1);
  border-radius: 16px;
  padding: 36px;
  text-align: center;
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out 0.6s backwards;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-blue) 0%, var(--primary-cyan) 50%, var(--primary-blue) 100%);
  background-size: 200% 100%;
  animation: shimmer 2s linear infinite;
}

.cta-content {
  position: relative;
  z-index: 1;
}

.cta-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px 0;
}

.cta-subtitle {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
}

.cta-indicator {
  margin-top: 20px;
  color: var(--primary-blue);
  animation: bounce 2s ease-in-out infinite;
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(8px);
  }
}

/* 聊天区域 */
.chat-area {
  flex: 1;
  padding: 16px;
  padding-top: 8px;
  overflow: hidden;
  position: relative;
  min-height: calc(100vh - 100px - 180px);
  margin-bottom: 16px;
}

.footer-container {
  position: relative;
  z-index: 1;
  margin-top: auto;
}

/* ============================================
   响应式设计
   ============================================ */
@media (max-width: 768px) {
  .glass-header {
    top: 12px;
    margin: 0 12px;
  }

  .header-content {
    padding: 12px 16px;
    gap: 12px;
  }

  .back-button {
    padding: 6px 10px;
    font-size: 13px;
    gap: 4px;
  }

  .back-button span {
    display: none;
  }

  .icon-wrapper {
    width: 36px;
    height: 36px;
  }

  .icon-wrapper svg {
    width: 20px;
    height: 20px;
  }

  .title {
    font-size: 16px;
  }

  .subtitle {
    font-size: 11px;
  }

  .header-spacer {
    width: 50px;
  }

  /* 特性展示区域 */
  .features-section {
    padding: 16px 12px;
  }

  .hero-section {
    margin-bottom: 32px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .capabilities-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    margin-bottom: 32px;
  }

  .capability-card {
    padding: 20px;
  }

  .card-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .card-icon {
    width: 24px;
    height: 24px;
  }

  .card-title {
    font-size: 17px;
  }

  .card-description {
    font-size: 13px;
  }

  .card-example {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .reasoning-demo {
    padding: 24px 20px;
  }

  .demo-title {
    font-size: 20px;
  }

  .demo-steps {
    flex-direction: column;
  }

  .step-arrow {
    display: none;
  }

  .demo-step {
    flex-direction: row;
    text-align: left;
    padding: 16px 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  }

  .demo-step:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  .demo-step:first-child {
    padding-top: 0;
  }

  .step-number {
    width: 36px;
    height: 36px;
    font-size: 16px;
    margin-bottom: 0;
    margin-right: 16px;
    flex-shrink: 0;
  }

  .step-content {
    text-align: left;
  }

  .step-title {
    font-size: 15px;
  }

  .step-text {
    font-size: 13px;
  }

  .cta-section {
    padding: 28px 20px;
  }

  .cta-title {
    font-size: 20px;
  }

  .cta-subtitle {
    font-size: 14px;
  }

  .chat-area {
    padding: 12px;
    padding-top: 4px;
    min-height: calc(100vh - 84px - 160px);
    margin-bottom: 12px;
  }
}

@media (max-width: 480px) {
  .glass-header {
    top: 8px;
    margin: 0 8px;
  }

  .header-content {
    padding: 10px 14px;
    border-radius: 10px;
  }

  .header-center {
    gap: 10px;
  }

  .icon-wrapper {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }

  .icon-wrapper svg {
    width: 18px;
    height: 18px;
  }

  .title {
    font-size: 15px;
  }

  .subtitle {
    display: none;
  }

  .header-spacer {
    width: 40px;
  }

  /* 特性展示区域 */
  .features-section {
    padding: 12px 8px;
  }

  .hero-section {
    margin-bottom: 24px;
  }

  .hero-badge {
    padding: 5px 12px;
    font-size: 12px;
    margin-bottom: 12px;
  }

  .hero-title {
    font-size: 24px;
  }

  .hero-subtitle {
    font-size: 14px;
  }

  .capabilities-grid {
    gap: 12px;
    margin-bottom: 24px;
  }

  .capability-card {
    padding: 16px;
  }

  .card-icon-wrapper {
    width: 44px;
    height: 44px;
    border-radius: 12px;
  }

  .card-icon {
    width: 22px;
    height: 22px;
  }

  .card-title {
    font-size: 16px;
    margin-bottom: 10px;
  }

  .card-description {
    font-size: 13px;
    margin-bottom: 12px;
  }

  .card-example {
    padding-top: 12px;
  }

  .reasoning-demo {
    padding: 20px 16px;
    border-radius: 12px;
  }

  .demo-header {
    margin-bottom: 20px;
  }

  .demo-title {
    font-size: 18px;
  }

  .demo-subtitle {
    font-size: 13px;
  }

  .demo-step {
    padding: 12px 0;
  }

  .step-number {
    width: 32px;
    height: 32px;
    font-size: 14px;
    margin-right: 12px;
  }

  .step-title {
    font-size: 14px;
    margin-bottom: 4px;
  }

  .step-text {
    font-size: 12px;
  }

  .cta-section {
    padding: 24px 16px;
    border-radius: 12px;
  }

  .cta-title {
    font-size: 18px;
    margin-bottom: 8px;
  }

  .cta-subtitle {
    font-size: 13px;
  }

  .chat-area {
    padding: 8px;
    min-height: calc(100vh - 70px - 150px);
    margin-bottom: 8px;
  }
}
</style>
