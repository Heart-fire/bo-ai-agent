<template>
  <div class="home-page">

    <!-- ── Sunrise: top-left origin ── -->
    <div class="ambient-bg" aria-hidden="true">
      <!-- Core sun -->
      <div class="sun-core"></div>
      <!-- Halo 1 -->
      <div class="sun-halo1"></div>
      <!-- Halo 2 -->
      <div class="sun-halo2"></div>
      <!-- Drifting warm blob -->
      <div class="drift1"></div>
      <!-- Drifting rose blob -->
      <div class="drift2"></div>

      <!-- Sun rays -->
      <div class="ray ray1"></div>
      <div class="ray ray2"></div>
      <div class="ray ray3"></div>
      <div class="ray ray4"></div>
      <div class="ray ray5"></div>

      <!-- Sparkles -->
      <div class="sparkle s1"></div>
      <div class="sparkle s2"></div>
      <div class="sparkle s3"></div>
      <div class="sparkle s4"></div>
      <div class="sparkle s5"></div>
      <div class="sparkle s6"></div>
      <div class="sparkle s7"></div>
      <div class="sparkle s8"></div>

      <!-- White fades -->
      <div class="fade-right"></div>
      <div class="fade-bottom"></div>
    </div>

    <!-- Header -->
    <header class="site-header">
      <div class="header-inner">
        <div class="brand">
          <div class="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <span class="brand-name">政策通</span>
        </div>
        <nav class="site-nav">
          <a href="#" class="nav-link">文档</a>
          <a href="#" class="nav-link">关于</a>
          <button class="nav-start-btn" @click="goToChat('qa')">立即开始</button>
        </nav>
      </div>
    </header>

    <!-- 主体 -->
    <main class="hero-main">

      <!-- AI 平台徽章 -->
      <div class="hero-badge">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2L2 7l10 5 10-5-10-5z"/>
          <path d="M2 17l10 5 10-5"/>
          <path d="M2 12l10 5 10-5"/>
        </svg>
        <span>AI 驱动的政策咨询平台</span>
      </div>

      <!-- 主标题 -->
      <h1 class="hero-title">你想了解什么政策？</h1>
      <p class="hero-subtitle">精准解读税收、社保、创业、就业等各类政策，帮你快速找到答案</p>

      <!-- 搜索框 -->
      <div class="search-wrap">
        <div class="search-box" :class="{ focused: isFocused }">
          <textarea
            ref="textareaRef"
            v-model="inputValue"
            @keydown="handleKeyDown"
            @input="handleInput"
            @focus="isFocused = true"
            @blur="isFocused = false"
            placeholder="输入你的政策问题，例如：个体工商户如何享受税收减免？"
            rows="1"
            class="search-textarea"
          ></textarea>

          <!-- 搜索框底部工具栏 -->
          <div class="search-toolbar">
            <div class="mode-btns">
              <!-- 政策问答模式 -->
              <button
                class="mode-btn"
                :class="{ 'mode-qa-active': selectedMode === 'qa' }"
                @click="toggleMode('qa')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
                </svg>
                政策问答
              </button>
              <!-- 深度分析模式 -->
              <button
                class="mode-btn"
                :class="{ 'mode-agent-active': selectedMode === 'agent' }"
                @click="toggleMode('agent')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
                </svg>
                深度分析
              </button>
            </div>
            <!-- 发送按钮 -->
            <button
              class="search-send-btn"
              :class="{ 'send-active': inputValue.trim() }"
              :disabled="!inputValue.trim()"
              @click="handleSend"
            >
              <img src="@/assets/025-发送.png" width="18" height="18" alt="发送" />
            </button>
          </div>
        </div>
      </div>

      <!-- 示例问题：点击填入输入框 -->
      <div class="examples-wrap">
        <p class="examples-label">试试这些问题</p>
        <div class="examples-list">
          <button
            v-for="(q, i) in EXAMPLE_QUESTIONS"
            :key="i"
            class="example-chip"
            @click="fillInput(q)"
          >
            {{ q }}
          </button>
        </div>
      </div>

      <!-- 模式卡片 -->
      <div class="mode-cards">
        <!-- 政策问答卡片 -->
        <button
          class="mode-card"
          :class="selectedMode === 'qa' ? 'card-qa-selected' : 'card-qa'"
          @click="toggleMode('qa')"
        >
          <div class="card-accent-line" :class="selectedMode === 'qa' ? 'accent-qa' : 'accent-idle'"></div>
          <div class="card-top">
            <div class="card-icon" :class="selectedMode === 'qa' ? 'icon-qa-active' : 'icon-idle'">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
              </svg>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              class="card-arrow" :class="selectedMode === 'qa' ? 'arrow-qa-active' : 'arrow-idle'">
              <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
            </svg>
          </div>
          <h3 class="card-title" :class="selectedMode === 'qa' ? 'title-qa-active' : 'title-idle'">⚡ 政策问答</h3>
          <p class="card-desc" :class="selectedMode === 'qa' ? 'desc-active' : 'desc-idle'">
            快速回答政策咨询，直接给出明确结论，适合查询具体规定
          </p>
          <div class="card-tags">
            <span class="card-tag" :class="selectedMode === 'qa' ? 'tag-qa' : 'tag-idle'">即时响应</span>
            <span class="card-tag" :class="selectedMode === 'qa' ? 'tag-qa' : 'tag-idle'">简洁明了</span>
          </div>
        </button>

        <!-- Agent 深度分析卡片 -->
        <button
          class="mode-card"
          :class="selectedMode === 'agent' ? 'card-agent-selected' : 'card-agent'"
          @click="toggleMode('agent')"
        >
          <div class="card-accent-line" :class="selectedMode === 'agent' ? 'accent-agent' : 'accent-idle'"></div>
          <div class="card-top">
            <div class="card-icon" :class="selectedMode === 'agent' ? 'icon-agent-active' : 'icon-idle'">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
              </svg>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              class="card-arrow" :class="selectedMode === 'agent' ? 'arrow-agent-active' : 'arrow-idle'">
              <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
            </svg>
          </div>
          <h3 class="card-title" :class="selectedMode === 'agent' ? 'title-agent-active' : 'title-idle'">📊 政策分析 Agent</h3>
          <p class="card-desc" :class="selectedMode === 'agent' ? 'desc-active' : 'desc-idle'">
            深度拆解政策背景、适用范围、影响分析，生成结构化报告
          </p>
          <div class="card-tags">
            <span class="card-tag" :class="selectedMode === 'agent' ? 'tag-agent' : 'tag-idle'">多步推理</span>
            <span class="card-tag" :class="selectedMode === 'agent' ? 'tag-agent' : 'tag-idle'">深度报告</span>
          </div>
        </button>
      </div>

      <!-- 特性标签 -->
      <div class="feature-tags">
        <div v-for="f in FEATURES" :key="f.text" class="feature-tag">
          <span>{{ f.icon }}</span>
          <span>{{ f.text }}</span>
        </div>
      </div>

    </main>

    <!-- 页脚 -->
    <footer class="site-footer">
      © 2026 政策通 · AI 政策咨询平台 · 仅供参考，具体以官方文件为准
    </footer>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'

useHead({
  title: '政策通 - AI 驱动的政策咨询平台',
  meta: [
    { name: 'description', content: '精准解读税收、社保、创业、就业等各类政策，帮你快速找到答案' },
    { name: 'keywords', content: 'AI政策问答,北京政策,社保查询,进京证,积分落户,政策助手' }
  ]
})

const router = useRouter()

const EXAMPLE_QUESTIONS = [
  '个人所得税起征点是多少？',
  '小微企业有哪些税收优惠政策？',
  '新能源汽车补贴政策最新规定？',
  '社保缴纳比例是多少？',
  '灵活就业人员如何缴纳社保？',
  '创业担保贷款怎么申请？',
]

const FEATURES = [
  { icon: '⚡', text: '实时政策解读' },
  { icon: '📋', text: '权威法规引用' },
  { icon: '🔍', text: '深度分析报告' },
  { icon: '💬', text: '自然语言问答' },
]

const inputValue = ref('')
const isFocused = ref(false)
const selectedMode = ref(null)   // 'qa' | 'agent' | null
const textareaRef = ref(null)

// 切换模式（再次点击则取消）
const toggleMode = (mode) => {
  selectedMode.value = selectedMode.value === mode ? null : mode
}

// 点击示例问题 → 填入输入框并聚焦
const fillInput = (q) => {
  inputValue.value = q
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.focus()
      handleInput()
    }
  })
}

// 自适应高度
const handleInput = () => {
  const ta = textareaRef.value
  if (!ta) return
  ta.style.height = 'auto'
  ta.style.height = Math.min(ta.scrollHeight, 160) + 'px'
}

// 键盘回车发送
const handleKeyDown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    if (inputValue.value.trim()) handleSend()
  }
}

// 跳转到对应聊天页面，带上初始问题（存 sessionStorage）
const goToChat = (mode, question) => {
  const q = question || inputValue.value
  const target = mode === 'agent' ? '/research-agent' : '/policy-chat'
  if (q && q.trim()) {
    sessionStorage.setItem('initQuestion', q.trim())
  } else {
    sessionStorage.removeItem('initQuestion')
  }
  router.push(target)
}

const handleSend = () => {
  if (!inputValue.value.trim()) return
  goToChat(selectedMode.value ?? 'qa')
}
</script>

<style scoped>
/* ============================================================
   全局 keyframes (在 scoped 中使用 :global 或直接定义)
   ============================================================ */
@keyframes sunPulse {
  0%   { opacity: 0.55; transform: scale(1)    translate(-50%, -50%); }
  50%  { opacity: 0.80; transform: scale(1.10) translate(-50%, -50%); }
  100% { opacity: 0.55; transform: scale(1)    translate(-50%, -50%); }
}
@keyframes halo1 {
  0%   { opacity: 0.30; transform: scale(1)    translate(-50%, -50%); }
  45%  { opacity: 0.50; transform: scale(1.12) translate(-50%, -50%); }
  100% { opacity: 0.30; transform: scale(1)    translate(-50%, -50%); }
}
@keyframes halo2 {
  0%   { opacity: 0.18; transform: scale(1)    translate(-50%, -50%); }
  55%  { opacity: 0.34; transform: scale(1.15) translate(-50%, -50%); }
  100% { opacity: 0.18; transform: scale(1)    translate(-50%, -50%); }
}
@keyframes drift1 {
  0%   { opacity: 0.22; transform: translate(0px, 0px) scale(1); }
  40%  { opacity: 0.38; transform: translate(60px, 40px) scale(1.1); }
  80%  { opacity: 0.25; transform: translate(20px, 80px) scale(0.95); }
  100% { opacity: 0.22; transform: translate(0px, 0px) scale(1); }
}
@keyframes drift2 {
  0%   { opacity: 0.16; transform: translate(0px, 0px) scale(1); }
  50%  { opacity: 0.28; transform: translate(80px, 60px) scale(1.08); }
  100% { opacity: 0.16; transform: translate(0px, 0px) scale(1); }
}
@keyframes ray {
  0%, 100% { opacity: 0.07; }
  50%       { opacity: 0.20; }
}
@keyframes glimmer {
  0%, 100% { opacity: 0; transform: scale(0.4); }
  50%       { opacity: 1; transform: scale(1); }
}

/* ============================================================
   容器 - 白色背景
   ============================================================ */
.home-page {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  color: #0f172a;
}

/* ============================================================
   Sunrise 背景层
   ============================================================ */
.ambient-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* Core sun — top-left corner origin */
.sun-core {
  position: absolute;
  top: 0%;
  left: 0%;
  width: 700px;
  height: 700px;
  background: radial-gradient(circle at 20% 20%,
    rgba(220,38,38,0.72) 0%,
    rgba(239,68,68,0.52) 20%,
    rgba(249,115,22,0.32) 45%,
    transparent 68%);
  filter: blur(32px);
  transform-origin: 0% 0%;
  animation: sunPulse 6s ease-in-out infinite;
  transform: translate(-50%, -50%);
}

.sun-halo1 {
  position: absolute;
  top: 0%;
  left: 0%;
  width: 1000px;
  height: 1000px;
  background: radial-gradient(circle at 18% 18%,
    rgba(249,115,22,0.38) 0%,
    rgba(251,146,60,0.20) 35%,
    transparent 62%);
  filter: blur(50px);
  transform-origin: 0% 0%;
  animation: halo1 8s ease-in-out infinite 0.5s;
  transform: translate(-50%, -50%);
}

.sun-halo2 {
  position: absolute;
  top: 0%;
  left: 0%;
  width: 1300px;
  height: 1300px;
  background: radial-gradient(circle at 15% 15%,
    rgba(251,191,36,0.22) 0%,
    rgba(252,211,77,0.10) 35%,
    transparent 58%);
  filter: blur(65px);
  transform-origin: 0% 0%;
  animation: halo2 11s ease-in-out infinite 1s;
  transform: translate(-50%, -50%);
}

.drift1 {
  position: absolute;
  top: 12%;
  left: 5%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(249,115,22,0.20) 0%, rgba(251,146,60,0.10) 50%, transparent 72%);
  filter: blur(55px);
  animation: drift1 13s ease-in-out infinite;
  border-radius: 50%;
}

.drift2 {
  position: absolute;
  top: 18%;
  left: 15%;
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, rgba(185,28,28,0.14) 0%, rgba(220,38,38,0.07) 50%, transparent 72%);
  filter: blur(60px);
  animation: drift2 17s ease-in-out infinite 2s;
  border-radius: 50%;
}

/* Sun rays */
.ray {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 2px;
  transform-origin: top left;
}
.ray1 { height: 75vh; background: linear-gradient(to bottom, rgba(239,68,68,0.22), transparent);  transform: rotate(25deg); animation: ray 4.0s ease-in-out infinite 0.0s; }
.ray2 { height: 68vh; background: linear-gradient(to bottom, rgba(249,115,22,0.18), transparent); transform: rotate(48deg); animation: ray 4.7s ease-in-out infinite 1.2s; }
.ray3 { height: 60vh; background: linear-gradient(to bottom, rgba(251,146,60,0.16), transparent); transform: rotate(70deg); animation: ray 5.4s ease-in-out infinite 0.6s; }
.ray4 { height: 55vh; background: linear-gradient(to bottom, rgba(220,38,38,0.14), transparent);  transform: rotate(92deg); animation: ray 6.1s ease-in-out infinite 1.8s; }
.ray5 { height: 58vh; background: linear-gradient(to bottom, rgba(239,68,68,0.16), transparent);  transform: rotate(7deg);  animation: ray 4.3s ease-in-out infinite 0.3s; }

/* Sparkles */
.sparkle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(239,68,68,0.95), rgba(251,146,60,0.5));
  box-shadow: 0 0 7px 3px rgba(239,68,68,0.35);
}
.s1 { top: 8%;  left: 12%; width: 5px; height: 5px; animation: glimmer 2.8s ease-in-out infinite 0.0s; }
.s2 { top: 14%; left: 28%; width: 3px; height: 3px; animation: glimmer 3.5s ease-in-out infinite 1.0s; }
.s3 { top: 22%; left: 8%;  width: 4px; height: 4px; animation: glimmer 3.2s ease-in-out infinite 0.5s; }
.s4 { top: 32%; left: 20%; width: 3px; height: 3px; animation: glimmer 4.0s ease-in-out infinite 1.7s; }
.s5 { top: 6%;  left: 42%; width: 4px; height: 4px; animation: glimmer 3.0s ease-in-out infinite 2.0s; }
.s6 { top: 40%; left: 55%; width: 3px; height: 3px; animation: glimmer 3.8s ease-in-out infinite 0.8s; }
.s7 { top: 18%; left: 60%; width: 3px; height: 3px; animation: glimmer 2.6s ease-in-out infinite 1.4s; }
.s8 { top: 50%; left: 75%; width: 4px; height: 4px; animation: glimmer 4.2s ease-in-out infinite 0.3s; }

/* White fade — right stays clean */
.fade-right {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 0%, transparent 40%, rgba(255,255,255,0.55) 65%, rgba(255,255,255,0.92) 100%);
}

/* White fade — bottom stays clean */
.fade-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 35%;
  background: linear-gradient(to top, rgba(255,255,255,0.88) 0%, transparent 100%);
}

/* ============================================================
   Header
   ============================================================ */
.site-header {
  position: relative;
  z-index: 10;
  width: 100%;
}

.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #ef4444, #f97316);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.35);
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  letter-spacing: -0.02em;
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

.nav-link:hover {
  color: #0f172a;
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

/* ============================================================
   主体 Hero 区域
   ============================================================ */
.hero-main {
  position: relative;
  z-index: 10;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px 48px;
}

/* 徽章 */
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
  margin-bottom: 32px;
  box-shadow: 0 1px 4px rgba(239, 68, 68, 0.06);
}

/* 主标题 */
.hero-title {
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: 700;
  line-height: 1.15;
  color: #0f172a;
  text-align: center;
  margin: 0 0 12px;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  font-size: 1.05rem;
  color: #64748b;
  text-align: center;
  margin: 0 0 40px;
  max-width: 420px;
  line-height: 1.6;
}

/* ============================================================
   搜索框
   ============================================================ */
.search-wrap {
  width: 100%;
  max-width: 672px;
  margin-bottom: 24px;
}

.search-box {
  position: relative;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-box.focused {
  border-color: #f87171;
  box-shadow: 0 8px 32px rgba(239, 68, 68, 0.12), 0 0 0 3px rgba(239, 68, 68, 0.08);
}

.search-textarea {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  resize: none;
  color: #0f172a;
  font-size: 0.95rem;
  line-height: 1.6;
  padding: 16px 20px 56px;
  font-family: inherit;
  max-height: 160px;
  overflow-y: hidden;
  box-sizing: border-box;
}

.search-textarea::placeholder {
  color: #94a3b8;
}

/* 搜索框工具栏 */
.search-toolbar {
  position: absolute;
  bottom: 10px;
  left: 14px;
  right: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mode-btns {
  display: flex;
  gap: 8px;
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #f1f5f9;
  color: #94a3b8;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.mode-btn:hover {
  color: #475569;
  border-color: #cbd5e1;
}

/* QA 激活 */
.mode-qa-active {
  background: #f59e0b !important;
  border-color: #fbbf24 !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.30);
}

/* Agent 激活 */
.mode-agent-active {
  background: #8b5cf6 !important;
  border-color: #a78bfa !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.30);
}

/* 发送按钮 */
.search-send-btn {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: none;
  background: #f1f5f9;
  color: #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: not-allowed;
  transition: all 0.15s ease;
  flex-shrink: 0;
}

/* 无输入时图标变暗 */
.search-send-btn img {
  filter: brightness(0) saturate(100%) invert(85%) sepia(5%) saturate(400%) hue-rotate(180deg) brightness(95%);
  transition: filter 0.15s ease;
}

.search-send-btn.send-active {
  background: #e86868;
  color: #fff;
  cursor: pointer;
}

/* 有输入时图标变白色（亮色） */
.search-send-btn.send-active img {
  filter: brightness(0) invert(1);
}

.search-send-btn.send-active:hover {
  background: #ec8181;
}

/* ============================================================
   示例问题
   ============================================================ */
.examples-wrap {
  width: 100%;
  max-width: 672px;
  margin-bottom: 48px;
  text-align: center;
}

.examples-label {
  font-size: 12px;
  color: #94a3b8;
  margin: 0 0 12px;
}

.examples-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.example-chip {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 100px;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.18s cubic-bezier(.34,1.56,.64,1);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.example-chip:hover {
  background: #fef2f2;
  color: #dc2626;
  border-color: #fecaca;
}

/* ============================================================
   模式卡片
   ============================================================ */
.mode-cards {
  width: 100%;
  max-width: 672px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 48px;
}

.mode-card {
  text-align: left;
  padding: 20px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  transition: transform 0.22s cubic-bezier(.34,1.56,.64,1), box-shadow 0.22s ease, background 0.18s ease, border-color 0.18s ease;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.mode-card:hover {
  transform: translateY(-4px) scale(1.015);
}

/* QA card states */
.card-qa:hover {
  border-color: #fde68a;
  background: rgba(245, 158, 11, 0.04);
  box-shadow: 0 12px 40px rgba(245, 158, 11, 0.18), 0 2px 8px rgba(0,0,0,0.06);
}
.card-qa-selected {
  background: linear-gradient(135deg, rgba(251,191,36,0.08) 0%, rgba(249,115,22,0.06) 100%);
  border-color: #fcd34d;
  box-shadow: 0 8px 32px rgba(245,158,11,0.22), 0 2px 8px rgba(0,0,0,0.05);
}

/* Agent card states */
.card-agent:hover {
  border-color: #ddd6fe;
  background: rgba(139, 92, 246, 0.04);
  box-shadow: 0 12px 40px rgba(168, 85, 247, 0.18), 0 2px 8px rgba(0,0,0,0.06);
}
.card-agent-selected {
  background: linear-gradient(135deg, rgba(139,92,246,0.08) 0%, rgba(109,40,217,0.06) 100%);
  border-color: #c4b5fd;
  box-shadow: 0 8px 32px rgba(168,85,247,0.22), 0 2px 8px rgba(0,0,0,0.05);
}

/* Card accent line (top) */
.card-accent-line {
  width: 100%;
  height: 2px;
  border-radius: 2px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}
.accent-idle  { background: #f1f5f9; opacity: 0.6; }
.accent-qa    { background: linear-gradient(to right, #f59e0b, #f97316); opacity: 1; }
.accent-agent { background: linear-gradient(to right, #8b5cf6, #7c3aed); opacity: 1; }

.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.22s cubic-bezier(.34,1.56,.64,1), background 0.18s ease;
}
.mode-card:hover .card-icon {
  transform: scale(1.12) rotate(-4deg);
}

.icon-idle       { background: #f1f5f9; color: #94a3b8; }
.icon-qa-active  { background: linear-gradient(135deg, #f59e0b, #f97316); color: #fff; box-shadow: 0 4px 12px rgba(245,158,11,0.3); }
.icon-agent-active { background: linear-gradient(135deg, #8b5cf6, #7c3aed); color: #fff; box-shadow: 0 4px 12px rgba(139,92,246,0.3); }

.card-arrow {
  transition: transform 0.22s cubic-bezier(.34,1.56,.64,1), color 0.18s ease, opacity 0.18s ease;
}
.arrow-idle        { color: #94a3b8; opacity: 0.3; }
.arrow-qa-active   { color: #f59e0b; opacity: 1; transform: translateX(3px); }
.arrow-agent-active { color: #8b5cf6; opacity: 1; transform: translateX(3px); }
.mode-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(3px);
}

.card-title { font-size: 0.95rem; font-weight: 600; margin: 0 0 6px; transition: color 0.2s; }
.title-idle        { color: #94a3b8; }
.title-qa-active   { color: #92400e; }
.title-agent-active { color: #5b21b6; }

.card-desc  { font-size: 0.8rem; line-height: 1.6; margin: 0 0 12px; transition: color 0.2s; }
.desc-idle   { color: #94a3b8; }
.desc-active { color: #475569; }

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.card-tag {
  padding: 2px 8px;
  border-radius: 100px;
  border: 1px solid;
  font-size: 11px;
  transition: background 0.18s, color 0.18s, border-color 0.18s;
}

.tag-idle  { background: #f8fafc; color: #94a3b8; border-color: #e2e8f0; }
.tag-qa    { background: rgba(245,158,11,0.1); color: #d97706; border-color: rgba(245,158,11,0.22); }
.tag-agent { background: rgba(139,92,246,0.1); color: #7c3aed; border-color: rgba(139,92,246,0.22); }

/* ============================================================
   特性标签栏
   ============================================================ */
.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #94a3b8;
}

/* ============================================================
   页脚
   ============================================================ */
.site-footer {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 24px 16px;
  font-size: 12px;
  color: #94a3b8;
}

/* ============================================================
   响应式
   ============================================================ */
@media (max-width: 640px) {
  .header-inner { padding: 16px; }
  .site-nav { gap: 16px; }
  .nav-link { display: none; }
  .hero-main { padding: 32px 12px 40px; }
  .hero-badge { margin-bottom: 24px; }
  .hero-subtitle { font-size: 0.95rem; margin-bottom: 28px; }
  .mode-cards { grid-template-columns: 1fr; }
  .examples-wrap { margin-bottom: 32px; }
  .mode-cards { margin-bottom: 32px; }
}

@media (max-width: 480px) {
  .search-textarea { padding: 14px 16px 52px; }
}
</style>
