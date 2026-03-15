<template>
  <div class="home">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="glow-orb glow-1"></div>
      <div class="glow-orb glow-2"></div>
      <div class="glow-orb glow-3"></div>
      <div class="grid-pattern"></div>
    </div>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- Hero 区域 -->
      <section class="hero">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          <span>AI Powered Platform</span>
        </div>
        <h1 class="hero-title">
          <span class="title-line">AI超级智能体</span>
          <span class="title-line title-accent">应用平台</span>
        </h1>
        <p class="hero-desc">
          探索AI的无限可能，让智能助手成为你生活与工作的得力伙伴
        </p>
      </section>

      <!-- 应用卡片区域 -->
      <section class="apps-section">
        <div
          class="app-card love-card"
          @click="navigateTo('/love-master')"
          v-animation="{ delay: 100 }"
        >
          <div class="card-background">
            <div class="card-gradient love-gradient"></div>
            <div class="card-glow"></div>
          </div>
          <div class="card-content">
            <div class="card-icon-wrapper">
              <div class="card-icon love-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
              </div>
            </div>
            <h3 class="card-title">AI恋爱大师</h3>
            <p class="card-desc">智能情感顾问，解答恋爱困惑，提供贴心建议</p>
            <div class="card-action">
              <span>开始对话</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>

        <div
          class="app-card agent-card"
          @click="navigateTo('/super-agent')"
          v-animation="{ delay: 200 }"
        >
          <div class="card-background">
            <div class="card-gradient agent-gradient"></div>
            <div class="card-glow"></div>
          </div>
          <div class="card-content">
            <div class="card-icon-wrapper">
              <div class="card-icon agent-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1v1a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-1H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2M7.5 13A1.5 1.5 0 0 0 6 14.5 1.5 1.5 0 0 0 7.5 16a1.5 1.5 0 0 0 1.5-1.5A1.5 1.5 0 0 0 7.5 13m9 0a1.5 1.5 0 0 0-1.5 1.5 1.5 1.5 0 0 0 1.5 1.5 1.5 1.5 0 0 0 1.5-1.5 1.5 1.5 0 0 0-1.5-1.5M12 9a5 5 0 0 0-5 5v1h10v-1a5 5 0 0 0-5-5z"/>
                </svg>
              </div>
            </div>
            <h3 class="card-title">AI超级智能体</h3>
            <p class="card-desc">全能型AI助手，解答各类专业问题</p>
            <div class="card-action">
              <span>开始对话</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>
      </section>

      <!-- 特性展示区域 -->
      <section class="features-section" v-animation="{ delay: 300 }">
        <div class="feature-item">
          <div class="feature-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
            </svg>
          </div>
          <div class="feature-content">
            <h4>极速响应</h4>
            <p>实时流式输出，秒级响应速度</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <div class="feature-content">
            <h4>多场景覆盖</h4>
            <p>情感咨询、专业问答等多种场景</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" y1="22.08" x2="12" y2="12"/>
            </svg>
          </div>
          <div class="feature-content">
            <h4>结构化输出</h4>
            <p>清晰的信息展示，更易理解</p>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import { onMounted } from 'vue'
import AppFooter from '../components/AppFooter.vue'

useHead({
  title: 'AI超级智能体应用平台',
  meta: [
    { name: 'description', content: '探索AI的无限可能，让智能助手成为你生活与工作的得力伙伴' },
    { name: 'keywords', content: 'AI智能体,AI助手,AI对话,人工智能' }
  ]
})

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}

// 动画指令
const vAnimation = {
  mounted(el, binding) {
    const delay = binding.value?.delay || 0
    el.style.opacity = '0'
    el.style.transform = 'translateY(20px)'
    el.style.transition = 'all 0.6s cubic-bezier(0.16, 1, 0.3, 1)'

    setTimeout(() => {
      el.style.opacity = '1'
      el.style.transform = 'translateY(0)'
    }, delay)
  }
}

onMounted(() => {
  // 页面加载完成后的入场动画
  document.body.classList.add('page-loaded')
})
</script>

<style scoped>
/* ============================================
   CSS 变量系统
   ============================================ */
.home {
  --primary-blue: #2b6fff;
  --primary-blue-light: #5b8fff;
  --primary-cyan: #06b6d4;
  --text-primary: #0f172a;
  --text-secondary: #475569;
  --text-tertiary: #94a3b8;
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  --border-light: #e2e8f0;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.04);
  --shadow-md: 0 4px 12px 0 rgb(0 0 0 / 0.06);
  --shadow-lg: 0 8px 24px 0 rgb(0 0 0 / 0.08);
  --shadow-xl: 0 16px 48px 0 rgb(0 0 0 / 0.12);

  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
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
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: orbFloat 20s ease-in-out infinite;
}

.glow-1 {
  width: clamp(400px, 30vw, 600px);
  height: clamp(400px, 30vw, 600px);
  background: radial-gradient(circle, rgba(43, 111, 255, 0.3) 0%, transparent 70%);
  top: clamp(-100px, -8vw, -200px);
  right: clamp(-100px, -8vw, -200px);
  animation-delay: 0s;
}

.glow-2 {
  width: clamp(350px, 25vw, 500px);
  height: clamp(350px, 25vw, 500px);
  background: radial-gradient(circle, rgba(6, 182, 212, 0.25) 0%, transparent 70%);
  bottom: clamp(-80px, -6vw, -150px);
  left: clamp(-80px, -6vw, -150px);
  animation-delay: -7s;
}

.glow-3 {
  width: clamp(300px, 20vw, 400px);
  height: clamp(300px, 20vw, 400px);
  background: radial-gradient(circle, rgba(168, 85, 247, 0.2) 0%, transparent 70%);
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

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(226, 232, 240, 0.5) 1px, transparent 1px),
    linear-gradient(90deg, rgba(226, 232, 240, 0.5) 1px, transparent 1px);
  background-size: clamp(40px, 5vw, 60px) clamp(40px, 5vw, 60px);
  mask-image: radial-gradient(ellipse 80% 50% at 50% 50%, black 40%, transparent 100%);
  -webkit-mask-image: radial-gradient(ellipse 80% 50% at 50% 50%, black 40%, transparent 100%);
}

/* ============================================
   主内容区
   ============================================ */
.main-content {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: 0 clamp(40px, 8vw, 120px);
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* ============================================
   Hero 区域
   ============================================ */
.hero {
  padding: clamp(60px, 10vh, 100px) 0 clamp(40px, 6vh, 70px);
  text-align: center;
  flex-shrink: 0;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: 100px;
  margin-bottom: clamp(24px, 3vh, 32px);
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: var(--primary-cyan);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.hero-title {
  font-size: clamp(32px, 5vw, 48px);
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.02em;
  margin-bottom: clamp(16px, 2.5vh, 24px);
}

.title-line {
  display: block;
  color: var(--text-primary);
}

.title-accent {
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-cyan) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: clamp(15px, 1.8vw, 17px);
  color: var(--text-secondary);
  line-height: 1.6;
  max-width: 540px;
  margin: 0 auto;
}

/* ============================================
   应用卡片区域
   ============================================ */
.apps-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: clamp(20px, 3vw, 32px);
  margin-bottom: clamp(40px, 6vh, 70px);
  flex: 1;
  align-content: center;
}

.app-card {
  position: relative;
  background: var(--bg-primary);
  border-radius: 16px;
  border: 1px solid var(--border-light);
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  min-height: 240px;
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.card-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.love-gradient {
  background: linear-gradient(135deg, rgba(255, 87, 127, 0.08) 0%, rgba(255, 158, 67, 0.04) 100%);
}

.agent-gradient {
  background: linear-gradient(135deg, rgba(43, 111, 255, 0.08) 0%, rgba(6, 182, 212, 0.04) 100%);
}

.card-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.6) 0%, transparent 70%);
  transform: translate(-50%, -50%);
}

.app-card:hover {
  border-color: transparent;
  box-shadow: var(--shadow-xl);
  transform: translateY(-4px);
}

.app-card:hover .card-background {
  opacity: 1;
}

.card-content {
  position: relative;
  padding: clamp(28px, 4vw, 40px) clamp(24px, 3vw, 36px);
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-icon-wrapper {
  margin-bottom: clamp(16px, 2.5vh, 24px);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.love-icon {
  background: linear-gradient(135deg, #ff577f 0%, #ff9e53 100%);
}

.agent-icon {
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-cyan) 100%);
}

.app-card:hover .card-icon {
  transform: scale(1.1) rotate(-3deg);
}

.card-title {
  font-size: clamp(18px, 2.5vw, 24px);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: clamp(8px, 1.5vh, 12px);
  letter-spacing: -0.01em;
}

.card-desc {
  font-size: clamp(13px, 1.5vw, 15px);
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: clamp(16px, 2.5vh, 24px);
}

.card-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: clamp(13px, 1.4vw, 14px);
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.app-card:hover .card-action {
  color: var(--text-primary);
  gap: 12px;
}

.card-action svg {
  transition: transform 0.3s ease;
  width: 16px;
  height: 16px;
}

.app-card:hover .card-action svg {
  transform: translateX(4px);
}

/* ============================================
   特性展示区域
   ============================================ */
.features-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: clamp(20px, 3vw, 32px);
  padding-bottom: clamp(30px, 5vh, 60px);
}

.feature-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.feature-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-blue);
  flex-shrink: 0;
}

.feature-icon svg {
  width: 18px;
  height: 18px;
}

.feature-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.feature-content p {
  font-size: 13px;
  color: var(--text-tertiary);
  line-height: 1.5;
}

/* ============================================
   响应式设计
   ============================================ */

/* 大屏幕优化 1600px+ */
@media (min-width: 1600px) {
  .main-content {
    padding: 0 clamp(80px, 10vw, 180px);
  }

  .hero-title {
    font-size: clamp(40px, 6vw, 64px);
  }

  .apps-section {
    gap: clamp(28px, 4vw, 40px);
  }

  .app-card {
    min-height: clamp(240px, 25vh, 300px);
  }

  .card-icon {
    width: clamp(56px, 6vw, 72px);
    height: clamp(56px, 6vw, 72px);
  }

  .card-title {
    font-size: clamp(20px, 2.5vw, 28px);
  }

  .card-desc {
    font-size: clamp(14px, 1.6vw, 16px);
  }
}

/* 1024px 及以下 - 平板 */
@media (max-width: 1024px) {
  .main-content {
    padding: 0 clamp(30px, 6vw, 50px);
  }

  .hero {
    padding: clamp(50px, 8vh, 70px) 0 clamp(30px, 5vh, 50px);
  }

  .apps-section {
    grid-template-columns: 1fr;
    gap: clamp(18px, 3vw, 24px);
    margin-bottom: clamp(30px, 5vh, 50px);
  }

  .app-card {
    min-height: 200px;
  }

  .card-content {
    padding: clamp(24px, 4vw, 32px) clamp(20px, 3vw, 28px);
  }

  .features-section {
    grid-template-columns: 1fr;
    gap: clamp(18px, 3vw, 24px);
    padding-bottom: clamp(30px, 5vh, 50px);
  }
}

/* 768px 及以下 - 移动设备 */
@media (max-width: 768px) {
  .main-content {
    padding: 0 clamp(20px, 5vw, 30px);
  }

  .hero {
    padding: clamp(40px, 7vh, 60px) 0 clamp(25px, 4vh, 40px);
  }

  .hero-badge {
    padding: 7px 14px;
    gap: 7px;
    font-size: 12px;
  }

  .badge-dot {
    width: 5px;
    height: 5px;
  }

  .hero-desc {
    font-size: 14px;
  }

  .card-content {
    padding: clamp(22px, 4vw, 28px) clamp(18px, 3vw, 24px);
  }

  .card-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
  }

  .card-icon svg {
    width: 24px;
    height: 24px;
  }

  .card-title {
    margin-bottom: 8px;
  }

  .card-desc {
    margin-bottom: 16px;
  }

  .card-action {
    gap: 6px;
  }

  .app-card:hover .card-action {
    gap: 10px;
  }

  .feature-item {
    gap: 10px;
  }

  .feature-icon {
    width: 32px;
    height: 32px;
  }

  .feature-icon svg {
    width: 16px;
    height: 16px;
  }

  .feature-content h4 {
    font-size: 14px;
  }

  .feature-content p {
    font-size: 12px;
  }
}

/* 480px 及以下 - 小屏手机 */
@media (max-width: 480px) {
  .main-content {
    padding: 0 clamp(16px, 4vw, 20px);
  }

  .hero {
    padding: clamp(30px, 6vh, 50px) 0 clamp(20px, 4vh, 30px);
  }

  .hero-badge {
    margin-bottom: 20px;
    padding: 6px 12px;
    font-size: 11px;
  }

  .hero-title {
    margin-bottom: 14px;
  }

  .apps-section {
    gap: 16px;
    margin-bottom: 24px;
  }

  .app-card {
    border-radius: 14px;
    min-height: 180px;
  }

  .card-content {
    padding: 20px 18px;
  }

  .card-icon-wrapper {
    margin-bottom: 14px;
  }

  .card-icon {
    width: 44px;
    height: 44px;
  }

  .card-icon svg {
    width: 22px;
    height: 22px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-desc {
    font-size: 13px;
    margin-bottom: 14px;
  }

  .card-action {
    font-size: 12px;
  }

  .card-action svg {
    width: 14px;
    height: 14px;
  }

  .features-section {
    gap: 16px;
    padding-bottom: 24px;
  }
}

/* 375px 及以下 - 超小屏 */
@media (max-width: 375px) {
  .main-content {
    padding: 0 14px;
  }

  .hero-badge {
    padding: 5px 10px;
    gap: 5px;
  }

  .card-content {
    padding: 18px 16px;
  }

  .card-icon {
    width: 40px;
    height: 40px;
  }

  .card-icon svg {
    width: 20px;
    height: 20px;
  }

  .card-title {
    font-size: 15px;
  }

  .feature-icon {
    width: 30px;
    height: 30px;
  }

  .feature-icon svg {
    width: 14px;
    height: 14px;
  }
}

/* ============================================
   页面加载动画
   ============================================ */
.home:not(.page-loaded) * {
  animation-play-state: paused;
}

.page-loaded .hero > * {
  animation: fadeInUp 0.6s ease-out forwards;
}

.page-loaded .hero-badge {
  animation-delay: 0s;
}

.page-loaded .hero-title {
  animation-delay: 0.1s;
}

.page-loaded .hero-desc {
  animation-delay: 0.2s;
}

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
</style>
