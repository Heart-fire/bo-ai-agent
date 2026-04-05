<template>
  <div class="love-master-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="glow-orb glow-1"></div>
      <div class="glow-orb glow-2"></div>
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
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
          </div>
          <div>
            <h1 class="title">AI恋爱大师</h1>
            <p class="subtitle">专业情感顾问 · 恋爱咨询助手</p>
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
            <div class="hero-badge">贴心陪伴</div>
            <h1 class="hero-title">AI 恋爱大师</h1>
            <p class="hero-subtitle">专业情感顾问，为你解答恋爱困惑</p>
          </div>

          <!-- 能力卡片网格 -->
          <div class="capabilities-grid">
            <!-- 卡片1: 情感分析 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-pink">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
              </div>
              <h3 class="card-title">情感分析</h3>
              <p class="card-description">深度解读情感状态，分析关系中的问题与机会，提供客观视角</p>
              <div class="card-example">
                <span class="example-label">功能：</span>
                <span class="example-text">关系诊断 · 情感评估 · 问题定位</span>
              </div>
            </div>

            <!-- 卡片2: 恋爱建议 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-rose">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                </svg>
              </div>
              <h3 class="card-title">恋爱建议</h3>
              <p class="card-description">基于心理学理论，提供实用的恋爱技巧和沟通策略</p>
              <div class="card-example">
                <span class="example-label">包括：</span>
                <span class="example-text">沟通技巧 · 约会建议 · 关系维护</span>
              </div>
            </div>

            <!-- 卡片3: 困惑解答 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-coral">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3"/>
                  <line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
              </div>
              <h3 class="card-title">困惑解答</h3>
              <p class="card-description">耐心倾听你的烦恼，用温暖的语言解答恋爱中的各种疑问</p>
              <div class="card-example">
                <span class="example-label">问题：</span>
                <span class="example-text">他喜欢我吗 · 如何表白 · 分手挽回</span>
              </div>
            </div>

            <!-- 卡片4: 场景覆盖 -->
            <div class="capability-card">
              <div class="card-icon-wrapper card-icon-peach">
                <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
              </div>
              <h3 class="card-title">全场景覆盖</h3>
              <p class="card-description">从单身到恋爱，从表白到婚姻，覆盖情感旅程的每个阶段</p>
              <div class="card-example">
                <span class="example-label">阶段：</span>
                <span class="example-text">单身 · 暧昧 · 恋爱 · 备婚 · 已婚</span>
              </div>
            </div>
          </div>

          <!-- 服务特色 -->
          <div class="service-features">
            <div class="features-header">
              <h2 class="features-title">为什么选择 AI 恋爱大师</h2>
              <p class="features-subtitle">专业、贴心、24小时在线的情感顾问</p>
            </div>
            <div class="features-list">
              <div class="feature-tag">
                <span class="tag-icon">🎯</span>
                <span class="tag-text">精准分析</span>
              </div>
              <div class="feature-tag">
                <span class="tag-icon">💝</span>
                <span class="tag-text">温暖共情</span>
              </div>
              <div class="feature-tag">
                <span class="tag-icon">🔒</span>
                <span class="tag-text">隐私保护</span>
              </div>
              <div class="feature-tag">
                <span class="tag-icon">⏰</span>
                <span class="tag-text">随时待命</span>
              </div>
              <div class="feature-tag">
                <span class="tag-icon">📚</span>
                <span class="tag-text">知识库支持</span>
              </div>
              <div class="feature-tag">
                <span class="tag-icon">💡</span>
                <span class="tag-text">实用建议</span>
              </div>
            </div>
          </div>

          <!-- 底部CTA -->
          <div class="cta-section">
            <div class="cta-content">
              <h2 class="cta-title">开始你的情感咨询</h2>
              <p class="cta-subtitle">在下方输入框倾诉你的困惑，让我来帮助你</p>
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
          ai-type="love"
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
import { chatWithLoveApp } from '../api'

// 设置页面标题和元数据
useHead({
  title: 'AI恋爱大师 - AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI恋爱大师是AI超级智能体应用平台的专业情感顾问，帮你解答各种恋爱问题，提供情感建议'
    },
    {
      name: 'keywords',
      content: 'AI恋爱大师,情感顾问,恋爱咨询,AI聊天,情感问题,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const chatId = ref('')
const connectionStatus = ref('disconnected')
let eventSource = null

// 生成随机会话ID
const generateChatId = () => {
  return 'love_' + Math.random().toString(36).substring(2, 10)
}

// 添加消息到列表
const addMessage = (content, isUser) => {
  messages.value.push({
    content,
    isUser,
    time: new Date().getTime()
  })
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true)

  // 连接SSE
  if (eventSource) {
    eventSource.close()
  }

  // 创建一个空的AI回复消息
  const aiMessageIndex = messages.value.length
  addMessage('', false)

  connectionStatus.value = 'connecting'
  eventSource = chatWithLoveApp(message, chatId.value)

  // 监听SSE消息
  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      // 更新最新的AI消息内容，而不是创建新消息
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content += data
      }
    }

    if (data === '[DONE]') {
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }

  // 监听SSE错误
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 生成聊天ID
  chatId.value = generateChatId()

  // 添加欢迎消息
  addMessage('欢迎来到AI恋爱大师，请告诉我你的恋爱问题，我会尽力给予帮助和建议。', false)
})

// 组件销毁前关闭SSE连接
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
.love-master-container {
  --primary-pink: #ff6b8b;
  --primary-pink-light: #ff9e53;
  --primary-pink-dark: #ff577f;
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
  background: linear-gradient(135deg, #fff9f9 0%, #ffe8ec 50%, #fff0f5 100%);
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
  background: radial-gradient(circle, rgba(255, 107, 139, 0.4) 0%, transparent 70%);
  top: -150px;
  right: -150px;
  animation-delay: 0s;
}

.glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 158, 83, 0.3) 0%, transparent 70%);
  bottom: -100px;
  left: -100px;
  animation-delay: -7s;
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
    radial-gradient(ellipse 80% 50% at 20% 40%, rgba(255, 107, 139, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse 60% 40% at 80% 60%, rgba(255, 158, 83, 0.06) 0%, transparent 50%);
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
  background: linear-gradient(135deg, var(--primary-pink) 0%, var(--primary-pink-light) 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 139, 0.3);
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
  background: linear-gradient(135deg, rgba(255, 107, 139, 0.1) 0%, rgba(255, 158, 83, 0.1) 100%);
  border: 1px solid rgba(255, 107, 139, 0.2);
  border-radius: 20px;
  color: var(--primary-pink);
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
  background: linear-gradient(135deg, var(--primary-pink) 0%, var(--primary-pink-light) 100%);
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

.card-icon-pink {
  background: linear-gradient(135deg, #ff6b8b 0%, #ff577f 100%);
  box-shadow: 0 8px 20px rgba(255, 107, 139, 0.3);
}

.card-icon-rose {
  background: linear-gradient(135deg, #fb7185 0%, #f43f5e 100%);
  box-shadow: 0 8px 20px rgba(251, 113, 133, 0.3);
}

.card-icon-coral {
  background: linear-gradient(135deg, #ff9e53 0%, #ff7c42 100%);
  box-shadow: 0 8px 20px rgba(255, 158, 83, 0.3);
}

.card-icon-peach {
  background: linear-gradient(135deg, #fda4af 0%, #fb7185 100%);
  box-shadow: 0 8px 20px rgba(253, 164, 175, 0.3);
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
  color: var(--primary-pink);
}

.example-text {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 服务特色 */
.service-features {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.5) 100%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 32px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  margin-bottom: 40px;
  animation: fadeInUp 0.6s ease-out 0.5s backwards;
}

.features-header {
  text-align: center;
  margin-bottom: 32px;
}

.features-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.features-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.features-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 107, 139, 0.15);
  border-radius: 100px;
  transition: all 0.3s ease;
}

.feature-tag:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(255, 107, 139, 0.3);
  transform: translateY(-2px);
}

.tag-icon {
  font-size: 18px;
}

.tag-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

/* CTA区域 */
.cta-section {
  background: linear-gradient(135deg, rgba(255, 107, 139, 0.08) 0%, rgba(255, 158, 83, 0.08) 100%);
  border: 1px solid rgba(255, 107, 139, 0.1);
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
  background: linear-gradient(90deg, var(--primary-pink) 0%, var(--primary-pink-light) 50%, var(--primary-pink) 100%);
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
  color: var(--primary-pink);
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

  .service-features {
    padding: 24px 20px;
  }

  .features-title {
    font-size: 20px;
  }

  .features-list {
    gap: 10px;
  }

  .feature-tag {
    padding: 10px 16px;
  }

  .tag-icon {
    font-size: 16px;
  }

  .tag-text {
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

  .service-features {
    padding: 20px 16px;
    border-radius: 12px;
  }

  .features-header {
    margin-bottom: 20px;
  }

  .features-title {
    font-size: 18px;
  }

  .features-subtitle {
    font-size: 13px;
  }

  .features-list {
    gap: 8px;
  }

  .feature-tag {
    padding: 8px 14px;
  }

  .tag-icon {
    font-size: 14px;
  }

  .tag-text {
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
