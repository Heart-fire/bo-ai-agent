<template>
  <div class="about-page">
    <!-- Header -->
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand">
          <div class="brand-icon">
            <img src="/政策指南针.png" width="27" height="27" alt="政策通" />
          </div>
          <span class="brand-name">政策通</span>
        </router-link>
        <nav class="site-nav">
          <router-link to="/docs" class="nav-link">文档</router-link>
          <router-link to="/about" class="nav-link active">关于</router-link>
          <button class="nav-start-btn" @click="$router.push('/policy-chat')">立即开始</button>
        </nav>
      </div>
    </header>

    <!-- Hero -->
    <section class="about-hero">
      <div class="hero-badge">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
        </svg>
        <span>关于我们</span>
      </div>
      <h1 class="hero-title">让每个人都能读懂政策</h1>
      <p class="hero-subtitle">
        政策通是一个 AI 驱动的政策咨询平台，致力于用自然语言交互的方式，帮助市民快速理解和获取政策信息。
      </p>
    </section>

    <!-- 使命 -->
    <section class="about-section">
      <div class="section-container">
        <div class="mission-grid">
          <div class="mission-card">
            <div class="mission-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
              </svg>
            </div>
            <h3>消除信息壁垒</h3>
            <p>政策文件专业术语多、条文复杂。我们用 AI 把晦涩的政策语言翻译成每个人都能理解的大白话。</p>
          </div>
          <div class="mission-card">
            <div class="mission-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
            </div>
            <h3>精准检索</h3>
            <p>基于 RAG（检索增强生成）技术，从权威政策文件中精准提取相关信息，确保回答有据可依。</p>
          </div>
          <div class="mission-card">
            <div class="mission-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
              </svg>
            </div>
            <h3>实时响应</h3>
            <p>基于 SSE 流式传输技术，AI 回答实时推送到你的屏幕，无需等待，如同与专家面对面交流。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 技术架构 -->
    <section class="about-section section-alt">
      <div class="section-container">
        <h2 class="section-heading">技术架构</h2>
        <p class="section-sub">我们采用前沿的 AI 技术栈，确保准确性和可靠性。</p>
        <div class="tech-grid">
          <div v-for="tech in techStack" :key="tech.name" class="tech-card">
            <div class="tech-icon" :style="{ background: tech.bg }">
              <span class="tech-emoji">{{ tech.icon }}</span>
            </div>
            <h4>{{ tech.name }}</h4>
            <p>{{ tech.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据来源 -->
    <section class="about-section">
      <div class="section-container">
        <h2 class="section-heading">数据来源</h2>
        <p class="section-sub">我们严格筛选信息来源，确保政策解读的权威性和时效性。</p>
        <div class="source-list">
          <div class="source-item">
            <div class="source-dot"></div>
            <div>
              <h4>北京市人民政府</h4>
              <p>北京市政府发布的官方政策文件、行政法规和通知公告</p>
            </div>
          </div>
          <div class="source-item">
            <div class="source-dot"></div>
            <div>
              <h4>各委办局官方网站</h4>
              <p>人社局、交通委、住建委、税务局等部门的公开政策信息</p>
            </div>
          </div>
          <div class="source-item">
            <div class="source-dot"></div>
            <div>
              <h4>国家政策法规</h4>
              <p>国务院及相关部委发布的全国性政策法规和指导文件</p>
            </div>
          </div>
          <div class="source-item">
            <div class="source-dot"></div>
            <div>
              <h4>实时采集更新</h4>
              <p>通过 AI Agent 自主采集最新公开信息，持续更新知识库</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 免责声明 -->
    <section class="about-section section-alt">
      <div class="section-container">
        <h2 class="section-heading">免责声明</h2>
        <div class="disclaimer-card">
          <div class="disclaimer-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          <div class="disclaimer-body">
            <p>政策通提供的所有政策解读和分析内容仅供参考，不构成任何法律建议。政策内容可能随时间变化，具体办理业务时请以政府部门最新官方文件为准。</p>
            <p>AI 生成的内容可能存在偏差，我们建议对关键政策信息进行二次核实。如因使用本平台内容产生任何决策，用户需自行承担相关责任。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="site-footer">
      © 2026 政策通 · AI 政策咨询平台 · 仅供参考，具体以官方文件为准
    </footer>
  </div>
</template>

<script setup>
import { useHead } from '@vueuse/head'

useHead({
  title: '关于 - 政策通',
  meta: [
    { name: 'description', content: '了解政策通：AI 驱动的政策咨询平台，让每个人都能读懂政策' }
  ]
})

const techStack = [
  { icon: '🤖', name: '大语言模型', desc: '基于先进 LLM 进行自然语言理解和政策解读', bg: 'linear-gradient(135deg, #fef3c7, #fde68a)' },
  { icon: '🔍', name: 'RAG 检索增强', desc: '从知识库精准检索相关政策片段，确保回答有据可依', bg: 'linear-gradient(135deg, #dbeafe, #bfdbfe)' },
  { icon: '🤝', name: 'AI Agent', desc: '自主采集、推理、分析的多步骤智能体，生成深度报告', bg: 'linear-gradient(135deg, #ede9fe, #ddd6fe)' },
  { icon: '⚡', name: 'SSE 流式推送', desc: '实时流式输出回答内容，打字机般的自然交互体验', bg: 'linear-gradient(135deg, #dcfce7, #bbf7d0)' },
  { icon: '💾', name: '向量知识库', desc: '政策文档向量化存储，语义级精准匹配相关政策内容', bg: 'linear-gradient(135deg, #fce7f3, #fbcfe8)' },
  { icon: '🧠', name: '多轮对话记忆', desc: '基于 PostgreSQL + Redis 的会话记忆，支持上下文连续对话', bg: 'linear-gradient(135deg, #ffedd5, #fed7aa)' },
]
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  color: #0f172a;
}

/* Header */
.site-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: rgba(240, 242, 246, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0,0,0,0.06);
}
.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}
.brand-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.site-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}
.nav-link {
  font-size: 14px;
  color: #64748b;
  text-decoration: none;
  transition: color 0.15s;
}
.nav-link:hover, .nav-link.active {
  color: #0f172a;
  font-weight: 500;
}
.nav-start-btn {
  padding: 6px 16px;
  border-radius: 100px;
  background: #0f172a;
  border: 1px solid #1e293b;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.15s ease;
}
.nav-start-btn:hover {
  background: #dc2626;
  border-color: #dc2626;
}

/* Hero */
.about-hero {
  text-align: center;
  padding: 64px 24px 48px;
  max-width: 680px;
  margin: 0 auto;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 100px;
  background: rgba(239, 68, 68, 0.07);
  border: 1px solid rgba(239, 68, 68, 0.22);
  color: #dc2626;
  font-size: 12px;
  margin-bottom: 24px;
}
.hero-title {
  font-size: clamp(1.8rem, 4vw, 2.5rem);
  font-weight: 700;
  margin: 0 0 16px;
  line-height: 1.2;
  letter-spacing: -0.02em;
}
.hero-subtitle {
  font-size: 1.05rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
}

/* Sections */
.about-section {
  padding: 56px 24px;
}
.section-alt {
  background: rgba(255,255,255,0.5);
}
.section-container {
  max-width: 960px;
  margin: 0 auto;
}
.section-heading {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 8px;
  text-align: center;
}
.section-sub {
  text-align: center;
  color: #64748b;
  font-size: 0.95rem;
  margin: 0 0 36px;
  line-height: 1.6;
}

/* Mission cards */
.mission-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.mission-card {
  padding: 28px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.2s;
}
.mission-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(0,0,0,0.08);
}
.mission-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #dc2626, #ef4444);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(220,38,38,0.25);
}
.mission-card h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 8px;
}
.mission-card p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
}

/* Tech grid */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.tech-card {
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}
.tech-card:hover {
  border-color: #fecaca;
  box-shadow: 0 4px 16px rgba(220,38,38,0.08);
}
.tech-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.tech-emoji {
  font-size: 1.3rem;
}
.tech-card h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin: 0 0 6px;
}
.tech-card p {
  font-size: 0.8rem;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

/* Source list */
.source-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 640px;
  margin: 0 auto;
}
.source-item {
  display: flex;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e2e8f0;
  align-items: flex-start;
}
.source-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dc2626;
  margin-top: 7px;
  flex-shrink: 0;
  box-shadow: 0 0 8px rgba(220,38,38,0.3);
}
.source-item h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin: 0 0 4px;
}
.source-item p {
  font-size: 0.8rem;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

/* Disclaimer */
.disclaimer-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #fcd34d;
  max-width: 720px;
  margin: 0 auto;
}
.disclaimer-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #fef9c3;
  color: #d97706;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.disclaimer-body p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0 0 8px;
}
.disclaimer-body p:last-child {
  margin-bottom: 0;
}

/* Footer */
.site-footer {
  text-align: center;
  padding: 24px 16px;
  font-size: 12px;
  color: #94a3b8;
  border-top: 1px solid rgba(0,0,0,0.06);
}

/* Responsive */
@media (max-width: 768px) {
  .mission-grid { grid-template-columns: 1fr; }
  .tech-grid { grid-template-columns: 1fr 1fr; }
  .about-hero { padding: 48px 20px 36px; }
}
@media (max-width: 640px) {
  .tech-grid { grid-template-columns: 1fr; }
  .site-nav { gap: 16px; }
  .nav-link { display: none; }
  .disclaimer-card { flex-direction: column; gap: 12px; }
}
</style>
