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
            <img src="/政策通知.png" width="27" height="27" alt="政策通" />
          </div>
          <span class="brand-name">政策通</span>
        </div>
        <nav class="site-nav">
          <router-link to="/docs" class="nav-link">文档</router-link>
          <router-link to="/about" class="nav-link">关于</router-link>
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
      <h1 class="hero-title">政策问题，一问就懂</h1>
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
            placeholder="输入你的问题，例如：社保缴纳比例是多少？"
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
            <div class="toolbar-right">
              <!-- 模型选择下拉 -->
              <div class="model-selector">
                <button class="model-select-btn" @click="toggleModelDropdown">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="4" y="4" width="16" height="16" rx="2"/>
                    <path d="M9 9h6v6H9z"/>
                  </svg>
                  {{ MODEL_OPTIONS.find(m => m.key === selectedModel)?.label }}
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                    :style="{ transform: showModelDropdown ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform 0.2s' }">
                    <polyline points="6 9 12 15 18 9"/>
                  </svg>
                </button>
                <div v-if="showModelDropdown" class="model-dropdown">
                  <button
                    v-for="m in MODEL_OPTIONS"
                    :key="m.key"
                    class="model-option"
                    :class="{ 'model-option-active': selectedModel === m.key }"
                    @click="selectModel(m.key)"
                  >
                    <span class="model-option-label">{{ m.label }}</span>
                    <svg v-if="selectedModel === m.key" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                  </button>
                </div>
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
        <button class="mode-card card-qa" @click="goToChat('qa')">
          <div class="card-top">
            <div class="card-icon icon-qa-active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
              </svg>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              class="card-arrow arrow-qa">
              <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
            </svg>
          </div>
          <!-- 内容区：hover 时整体上移，露出底部提示 -->
          <div class="card-body">
            <h3 class="card-title title-qa-active">⚡ 政策问答</h3>
            <p class="card-desc desc-active">
              快速回答政策咨询，直接给出明确结论，适合查询具体规定
            </p>
            <div class="card-tags">
              <span class="card-tag tag-qa">即时响应</span>
              <span class="card-tag tag-qa">简洁明了</span>
            </div>
            <!-- 立刻体验提示：默认隐藏，hover 淡入 -->
            <div class="card-cta cta-qa">
              <span>立刻体验</span>
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
              </svg>
            </div>
          </div>
        </button>

        <!-- Agent 深度分析卡片 -->
        <button class="mode-card card-agent" @click="goToChat('agent')">
          <div class="card-top">
            <div class="card-icon icon-agent-active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
              </svg>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              class="card-arrow arrow-agent">
              <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
            </svg>
          </div>
          <div class="card-body">
            <h3 class="card-title title-agent-active">📊 政策分析 Agent</h3>
            <p class="card-desc desc-active">
              深度拆解政策背景、适用范围、影响分析，生成结构化报告
            </p>
            <div class="card-tags">
              <span class="card-tag tag-agent">多步推理</span>
              <span class="card-tag tag-agent">深度报告</span>
            </div>
            <div class="card-cta cta-agent">
              <span>立刻体验</span>
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
              </svg>
            </div>
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
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
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

// 模型选择
const MODEL_OPTIONS = [
  { key: 'dashscope', label: 'Qwen3-Max' },
  { key: 'deepseek', label: 'DeepSeek' },
]
const selectedModel = ref('dashscope')
const showModelDropdown = ref(false)

const toggleModelDropdown = () => {
  showModelDropdown.value = !showModelDropdown.value
}

const selectModel = (key) => {
  selectedModel.value = key
  showModelDropdown.value = false
}

// 点击外部关闭模型下拉
const onDocClick = (e) => {
  if (showModelDropdown.value && !e.target.closest('.model-selector')) {
    showModelDropdown.value = false
  }
}
onMounted(() => document.addEventListener('click', onDocClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocClick))

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

// 跳转到对应聊天页面，带上初始问题和模型（存 sessionStorage）
const goToChat = (mode, question) => {
  const q = question || inputValue.value
  const target = mode === 'agent' ? '/research-agent' : '/policy-chat'
  if (q && q.trim()) {
    sessionStorage.setItem('initQuestion', q.trim())
  } else {
    sessionStorage.removeItem('initQuestion')
  }
  sessionStorage.setItem('initModel', selectedModel.value)
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
  background: #FFFFFF;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-name {
  font-size: 20px;
  font-weight: 650;
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

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 模型选择器 */
.model-selector {
  position: relative;
}

.model-select-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s ease;
  white-space: nowrap;
}

.model-select-btn:hover {
  color: #334155;
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.model-dropdown {
  position: absolute;
  bottom: calc(100% + 6px);
  right: 0;
  min-width: 140px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 4px;
  z-index: 100;
  animation: dropIn 0.15s ease-out;
}

@keyframes dropIn {
  from { opacity: 0; transform: translateY(4px); }
  to   { opacity: 1; transform: translateY(0); }
}

.model-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #475569;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.12s ease;
}

.model-option:hover {
  background: #f8fafc;
  color: #0f172a;
}

.model-option-active {
  color: #0f172a;
  font-weight: 500;
  background: #f1f5f9;
}

.model-option-label {
  display: flex;
  align-items: center;
  gap: 6px;
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

/* 卡片基础 */
.mode-card {
  text-align: left;
  padding: 20px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  overflow: hidden;
  transition:
    transform 0.26s cubic-bezier(.34,1.56,.64,1),
    box-shadow 0.24s ease,
    background 0.2s ease,
    border-color 0.2s ease;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

/* ── 顶部图标行 ── */
.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.26s cubic-bezier(.34,1.56,.64,1);
}
.icon-qa-active    { background: linear-gradient(135deg, #f59e0b, #f97316); color: #fff; box-shadow: 0 4px 12px rgba(245,158,11,0.3); }
.icon-agent-active { background: linear-gradient(135deg, #8b5cf6, #7c3aed); color: #fff; box-shadow: 0 4px 12px rgba(139,92,246,0.3); }

.card-arrow {
  transition: transform 0.24s cubic-bezier(.34,1.56,.64,1), opacity 0.2s ease;
  opacity: 0.45;
}
.arrow-qa    { color: #f59e0b; }
.arrow-agent { color: #8b5cf6; }

/* ── 内容区：clip 裁剪 + 整体上移让 CTA 露出 ── */
.card-body {
  display: flex;
  flex-direction: column;
  gap: 0;
  /* 多留 36px 给 CTA，平时被父级 overflow:hidden 裁掉 */
  transform: translateY(0);
  transition: transform 0.28s cubic-bezier(.34,1.56,.64,1);
}

.card-title {
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0 0 6px;
}
.title-qa-active    { color: #92400e; }
.title-agent-active { color: #5b21b6; }

.card-desc {
  font-size: 0.8rem;
  line-height: 1.6;
  margin: 0 0 10px;
  color: #475569;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.card-tag {
  padding: 2px 8px;
  border-radius: 100px;
  border: 1px solid;
  font-size: 11px;
}
.tag-qa    { background: rgba(245,158,11,0.10); color: #d97706; border-color: rgba(245,158,11,0.25); }
.tag-agent { background: rgba(139,92,246,0.10); color: #7c3aed; border-color: rgba(139,92,246,0.25); }

/* ── 立刻体验 CTA ── */
.card-cta {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 600;
  opacity: 0;
  transform: translateY(4px);
  transition: opacity 0.22s ease, transform 0.22s ease;
}
.cta-qa    { color: #d97706; }
.cta-agent { color: #7c3aed; }

/* ── QA 卡片 hover ── */
.card-qa {
  background: linear-gradient(135deg, rgba(251,191,36,0.07) 0%, rgba(249,115,22,0.05) 100%);
  border-color: #fcd34d;
  box-shadow: 0 6px 28px rgba(245,158,11,0.18), 0 2px 8px rgba(0,0,0,0.05);
}
.card-qa:hover {
  transform: translateY(-5px) scale(1.018);
  background: linear-gradient(135deg, rgba(251,191,36,0.14) 0%, rgba(249,115,22,0.10) 100%);
  border-color: #fbbf24;
  box-shadow: 0 16px 48px rgba(245,158,11,0.28), 0 4px 12px rgba(0,0,0,0.06);
}
.card-qa:hover .card-icon    { transform: scale(1.12) rotate(-5deg); }
.card-qa:hover .card-arrow   { opacity: 1; transform: translateX(4px); }
.card-qa:hover .card-body    { transform: translateY(-14px); }
.card-qa:hover .card-cta     { opacity: 1; transform: translateY(0); }

/* ── Agent 卡片 hover ── */
.card-agent {
  background: linear-gradient(135deg, rgba(139,92,246,0.07) 0%, rgba(109,40,217,0.05) 100%);
  border-color: #c4b5fd;
  box-shadow: 0 6px 28px rgba(139,92,246,0.18), 0 2px 8px rgba(0,0,0,0.05);
}
.card-agent:hover {
  transform: translateY(-5px) scale(1.018);
  background: linear-gradient(135deg, rgba(139,92,246,0.13) 0%, rgba(109,40,217,0.09) 100%);
  border-color: #a78bfa;
  box-shadow: 0 16px 48px rgba(139,92,246,0.28), 0 4px 12px rgba(0,0,0,0.06);
}
.card-agent:hover .card-icon  { transform: scale(1.12) rotate(-5deg); }
.card-agent:hover .card-arrow { opacity: 1; transform: translateX(4px); }
.card-agent:hover .card-body  { transform: translateY(-14px); }
.card-agent:hover .card-cta   { opacity: 1; transform: translateY(0); }


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
