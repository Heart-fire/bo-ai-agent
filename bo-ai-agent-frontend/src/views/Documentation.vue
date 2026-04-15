<template>
  <div class="doc-page">
    <!-- Header -->
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand">
          <div class="brand-icon">
            <img src="/政策通知.png" width="27" height="27" alt="政策通" />
          </div>
          <span class="brand-name">政策通</span>
        </router-link>
        <nav class="site-nav">
          <router-link to="/docs" class="nav-link active">文档</router-link>
          <router-link to="/about" class="nav-link">关于</router-link>
          <button class="nav-start-btn" @click="$router.push('/policy-chat')">立即开始</button>
        </nav>
      </div>
    </header>

    <!-- Main -->
    <main class="doc-main">
      <div class="doc-container">
        <!-- Sidebar -->
        <aside class="doc-sidebar">
          <div class="sidebar-sticky">
            <h3 class="sidebar-title">目录</h3>
            <ul class="sidebar-nav">
              <li v-for="section in sections" :key="section.id">
                <a
                  :href="'#' + section.id"
                  class="sidebar-link"
                  :class="{ active: activeSection === section.id }"
                  @click.prevent="scrollTo(section.id)"
                >{{ section.title }}</a>
              </li>
            </ul>
          </div>
        </aside>

        <!-- Content -->
        <div class="doc-content">
          <!-- 快速开始 -->
          <section :id="sections[0].id" class="doc-section">
            <h2 class="section-title">{{ sections[0].title }}</h2>
            <p class="section-desc">政策通提供两种核心能力，帮助你快速获取和理解政策信息。</p>
            <div class="feature-grid">
              <div class="feature-card feature-qa">
                <div class="feature-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
                  </svg>
                </div>
                <h3>政策问答</h3>
                <p>基于 RAG 知识库的即时问答，快速获取政策答案。适合查询具体规定、条件、流程等。</p>
                <button class="feature-btn btn-qa" @click="$router.push('/policy-chat')">开始使用</button>
              </div>
              <div class="feature-card feature-agent">
                <div class="feature-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
                  </svg>
                </div>
                <h3>深度分析</h3>
                <p>AI Agent 自主采集公开信息，多步推理生成结构化研究报告，适合深入分析政策影响。</p>
                <button class="feature-btn btn-agent" @click="$router.push('/research-agent')">开始使用</button>
              </div>
            </div>
          </section>

          <!-- 使用指南 -->
          <section :id="sections[1].id" class="doc-section">
            <h2 class="section-title">{{ sections[1].title }}</h2>
            <div class="steps">
              <div class="step">
                <div class="step-number">1</div>
                <div class="step-body">
                  <h3>输入你的问题</h3>
                  <p>在首页搜索框或聊天页面中，用自然语言描述你想了解的政策问题。例如："社保缴纳比例是多少？"</p>
                </div>
              </div>
              <div class="step">
                <div class="step-number">2</div>
                <div class="step-body">
                  <h3>选择回答模式</h3>
                  <p>
                    <strong>政策问答</strong> — 快速获取精简回答，适合查询具体规定。<br>
                    <strong>深度分析</strong> — Agent 多步推理，生成详细分析报告。
                  </p>
                </div>
              </div>
              <div class="step">
                <div class="step-number">3</div>
                <div class="step-body">
                  <h3>获取答案</h3>
                  <p>AI 实时流式输出回答，支持 Markdown 格式。你可以继续追问，系统会记住上下文进行多轮对话。</p>
                </div>
              </div>
            </div>
          </section>

          <!-- 支持的政策领域 -->
          <section :id="sections[2].id" class="doc-section">
            <h2 class="section-title">{{ sections[2].title }}</h2>
            <div class="domain-grid">
              <div v-for="domain in domains" :key="domain.name" class="domain-card">
                <span class="domain-emoji">{{ domain.icon }}</span>
                <div>
                  <h4>{{ domain.name }}</h4>
                  <p>{{ domain.desc }}</p>
                </div>
              </div>
            </div>
          </section>

          <!-- API 文档 -->
          <section :id="sections[3].id" class="doc-section">
            <h2 class="section-title">{{ sections[3].title }}</h2>
            <p class="section-desc">如果你需要将政策通集成到自己的应用中，可以使用以下 API 接口。</p>

            <div class="api-block">
              <div class="api-header">
                <span class="api-method post">POST</span>
                <code class="api-url">/api/policy/chat</code>
              </div>
              <p class="api-desc">政策问答接口，基于 SSE 流式返回结果。</p>
              <div class="code-block">
                <pre><code>// 请求示例
const response = await fetch('/api/policy/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    message: '社保缴纳比例是多少？',
    chatId: 'your-chat-id'
  })
})

// SSE 流式读取
const reader = response.body.getReader()
const decoder = new TextDecoder()</code></pre>
              </div>
            </div>

            <div class="api-block">
              <div class="api-header">
                <span class="api-method post">POST</span>
                <code class="api-url">/api/agent/chat</code>
              </div>
              <p class="api-desc">深度分析接口，Agent 自主采集信息并生成研究报告。</p>
              <div class="code-block">
                <pre><code>// 请求示例
const response = await fetch('/api/agent/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    message: '分析2024年小微企业税收优惠政策',
    chatId: 'your-chat-id'
  })
})</code></pre>
              </div>
            </div>
          </section>

          <!-- 常见问题 -->
          <section :id="sections[4].id" class="doc-section">
            <h2 class="section-title">{{ sections[4].title }}</h2>
            <div class="faq-list">
              <div v-for="(faq, i) in faqs" :key="i" class="faq-item">
                <button class="faq-question" @click="toggleFaq(i)">
                  <span>{{ faq.q }}</span>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    :style="{ transform: openFaqs.includes(i) ? 'rotate(180deg)' : '' }" class="faq-arrow">
                    <polyline points="6 9 12 15 18 9"/>
                  </svg>
                </button>
                <div class="faq-answer" v-show="openFaqs.includes(i)">
                  <p>{{ faq.a }}</p>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="site-footer">
      © 2026 政策通 · AI 政策咨询平台 · 仅供参考，具体以官方文件为准
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useHead } from '@vueuse/head'

useHead({
  title: '文档 - 政策通',
  meta: [
    { name: 'description', content: '政策通使用文档：快速开始、使用指南、API 文档和常见问题' }
  ]
})

const sections = [
  { id: 'quick-start', title: '快速开始' },
  { id: 'guide', title: '使用指南' },
  { id: 'domains', title: '支持的政策领域' },
  { id: 'api', title: 'API 文档' },
  { id: 'faq', title: '常见问题' },
]

const domains = [
  { icon: '🏥', name: '社保公积金', desc: '缴纳比例、灵活就业、转移接续' },
  { icon: '🚗', name: '交通出行', desc: '进京证、摇号、限行政策' },
  { icon: '🏠', name: '住房安居', desc: '公租房、共有产权、购房政策' },
  { icon: '📊', name: '积分落户', desc: '积分规则、申报流程、分数线' },
  { icon: '💼', name: '创业就业', desc: '创业补贴、贷款优惠、就业扶持' },
  { icon: '💰', name: '税收优惠', desc: '小微企业、个税专项、减税降费' },
  { icon: '👨‍🎓', name: '教育就学', desc: '入学政策、学区划分、教育补贴' },
  { icon: '⚕️', name: '医疗健康', desc: '医保报销、异地就医、大病保障' },
]

const faqs = [
  { q: '政策通提供的信息准确吗？', a: '政策通基于权威政府网站和政策文件构建知识库，AI 会引用信息来源。但由于政策经常更新，建议在办理具体业务前以官方最新文件为准。' },
  { q: '我的对话数据安全吗？', a: '对话数据仅用于维持会话上下文，不会用于训练模型或分享给第三方。聊天记录存储在本地和加密数据库中。' },
  { q: '支持哪些地区的政策？', a: '目前主要覆盖北京市级和区级政策，包括社保、交通、住房、积分落户等领域。后续将逐步扩展到更多城市。' },
  { q: '深度分析和政策问答有什么区别？', a: '政策问答适合快速查询具体问题，直接给出精简答案；深度分析由 AI Agent 自主搜索、采集、推理，生成结构化的详细分析报告，适合需要全面了解政策背景的场景。' },
  { q: '可以商用吗？', a: '目前政策通仅供个人学习和参考使用。如需商业合作或 API 接入，请联系我们。' },
]

const activeSection = ref('quick-start')
const openFaqs = ref([])

const scrollTo = (id) => {
  activeSection.value = id
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const toggleFaq = (i) => {
  const idx = openFaqs.value.indexOf(i)
  if (idx >= 0) openFaqs.value.splice(idx, 1)
  else openFaqs.value.push(i)
}
</script>

<style scoped>
.doc-page {
  min-height: 100vh;
  background: #F0F2F6;
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

/* Main layout */
.doc-main {
  flex: 1;
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 24px;
}
.doc-container {
  display: flex;
  gap: 48px;
}

/* Sidebar */
.doc-sidebar {
  width: 200px;
  flex-shrink: 0;
}
.sidebar-sticky {
  position: sticky;
  top: 80px;
}
.sidebar-title {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 16px;
}
.sidebar-nav {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.sidebar-link {
  display: block;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #64748b;
  text-decoration: none;
  transition: all 0.15s;
}
.sidebar-link:hover {
  color: #0f172a;
  background: rgba(0,0,0,0.04);
}
.sidebar-link.active {
  color: #dc2626;
  background: rgba(220, 38, 38, 0.06);
  font-weight: 500;
}

/* Content */
.doc-content {
  flex: 1;
  min-width: 0;
}
.doc-section {
  margin-bottom: 56px;
  scroll-margin-top: 80px;
}
.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 16px;
  color: #0f172a;
}
.section-desc {
  font-size: 0.95rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0 0 24px;
}

/* Feature grid */
.feature-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.feature-card {
  padding: 24px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.feature-card h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 12px 0 8px;
}
.feature-card p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
}
.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.feature-qa .feature-icon {
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
  box-shadow: 0 4px 12px rgba(245,158,11,0.25);
}
.feature-agent .feature-icon {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: #fff;
  box-shadow: 0 4px 12px rgba(139,92,246,0.25);
}
.feature-btn {
  padding: 6px 16px;
  border-radius: 8px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-qa {
  background: rgba(245,158,11,0.12);
  color: #d97706;
}
.btn-qa:hover {
  background: #f59e0b;
  color: #fff;
}
.btn-agent {
  background: rgba(139,92,246,0.12);
  color: #7c3aed;
}
.btn-agent:hover {
  background: #8b5cf6;
  color: #fff;
}

/* Steps */
.steps {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.step {
  display: flex;
  gap: 20px;
  padding: 24px 0;
  border-bottom: 1px solid #e2e8f0;
}
.step:last-child {
  border-bottom: none;
}
.step-number {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #dc2626, #ef4444);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(220,38,38,0.25);
}
.step-body h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 6px;
}
.step-body p {
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
}

/* Domain grid */
.domain-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.domain-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e2e8f0;
  transition: all 0.15s;
}
.domain-card:hover {
  border-color: #fecaca;
  box-shadow: 0 4px 16px rgba(220,38,38,0.08);
}
.domain-emoji {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: 2px;
}
.domain-card h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin: 0 0 4px;
}
.domain-card p {
  font-size: 0.8rem;
  color: #94a3b8;
  margin: 0;
}

/* API blocks */
.api-block {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
  overflow: hidden;
}
.api-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}
.api-method {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}
.api-method.post {
  background: #dcfce7;
  color: #16a34a;
}
.api-url {
  font-size: 13px;
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: #334155;
}
.api-desc {
  padding: 12px 20px 0;
  font-size: 0.85rem;
  color: #64748b;
  margin: 0;
}
.code-block {
  margin: 12px 20px 20px;
  border-radius: 8px;
  background: #1e293b;
  overflow-x: auto;
}
.code-block pre {
  margin: 0;
  padding: 16px;
}
.code-block code {
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #e2e8f0;
}

/* FAQ */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.faq-item {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
  overflow: hidden;
}
.faq-question {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  color: #0f172a;
  text-align: left;
}
.faq-question:hover {
  background: rgba(0,0,0,0.02);
}
.faq-arrow {
  transition: transform 0.2s;
  flex-shrink: 0;
  color: #94a3b8;
}
.faq-answer {
  padding: 0 20px 16px;
}
.faq-answer p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
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
  .doc-container { flex-direction: column; gap: 24px; }
  .doc-sidebar { width: 100%; }
  .sidebar-sticky { position: static; }
  .sidebar-nav { flex-direction: row; flex-wrap: wrap; gap: 4px; }
  .sidebar-link { padding: 6px 10px; font-size: 13px; }
  .feature-grid, .domain-grid { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .site-nav { gap: 16px; }
  .nav-link { display: none; }
}
</style>
