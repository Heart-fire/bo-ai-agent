<template>
  <div class="structured-message">
    <!-- TOOL_RESULT: 工具执行结果 -->
    <template v-if="message.messageType === 'TOOL_RESULT'">
      <!-- 思考气泡 -->
      <Transition name="thought-fade">
        <div v-if="firstItemThought" class="thought-bubble">
          <div class="thought-indicator">
            <span class="pulse-dot"></span>
            <span class="thought-label">思考中</span>
          </div>
          <p class="thought-content">{{ firstItemThought }}</p>
        </div>
      </Transition>

      <!-- 卡片结果 -->
      <TransitionGroup name="card-slide" tag="div" class="cards-container">
        <div
          v-for="(result, index) in cardResults"
          :key="`card-${index}`"
          class="card-wrapper"
          :style="{ '--delay': `${index * 80}ms` }"
        >
          <div class="card-tag">
            <span class="tag-icon">{{ getToolIcon(result.toolName) }}</span>
            <span class="tag-text">{{ getToolDisplayName(result.toolName) }}</span>
          </div>
          <InfoCard :card="result.card" />
        </div>
      </TransitionGroup>
    </template>

    <!-- SUMMARY: 任务总结 -->
    <template v-else-if="message.messageType === 'SUMMARY'">
      <Transition name="summary-pop">
        <div class="summary-container">
          <div class="summary-header">
            <div class="success-icon-wrapper">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M20 6L9 17l-5-5"/>
              </svg>
            </div>
            <h3 class="summary-title">已完成</h3>
          </div>

          <p class="summary-conclusion">{{ summaryData.conclusion }}</p>

          <div v-if="summaryData.toolsUsed?.length" class="tools-row">
            <span class="tools-label">使用工具</span>
            <div class="tools-pills">
              <span
                v-for="(tool, idx) in summaryData.toolsUsed"
                :key="idx"
                class="tool-pill"
              >
                {{ getToolDisplayName(tool) }}
              </span>
            </div>
          </div>

          <div v-if="summaryData.cards?.length" class="summary-cards">
            <InfoCard
              v-for="(card, idx) in summaryData.cards"
              :key="`summary-${idx}`"
              :card="card"
              :compact="true"
            />
          </div>
        </div>
      </Transition>
    </template>

    <!-- ERROR: 错误信息 -->
    <template v-else-if="message.messageType === 'ERROR'">
      <Transition name="error-shake">
        <div class="error-container">
          <div class="error-icon-wrapper">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 8v4M12 16h.01"/>
            </svg>
          </div>
          <p class="error-text">{{ errorData.message }}</p>
        </div>
      </Transition>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import InfoCard from './InfoCard.vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const data = computed(() => props.message.data || [])

const firstItemThought = computed(() => {
  if (Array.isArray(data.value) && data.value[0]?.thought) {
    return data.value[0].thought
  }
  return null
})

const cardResults = computed(() => {
  if (Array.isArray(data.value)) {
    return data.value.filter(item => item.card)
  }
  return []
})

const summaryData = computed(() => props.message.data)
const errorData = computed(() => props.message.data)

const toolDisplayNames = {
  webSearch: '搜索',
  webScraping: '网页解析',
  resourceDownload: '下载资源',
  search: '搜索'
}

const toolIcons = {
  webSearch: '🔍',
  webScraping: '📄',
  resourceDownload: '⬇️',
  search: '🔍'
}

const getToolDisplayName = (name) => toolDisplayNames[name] || name
const getToolIcon = (name) => toolIcons[name] || '⚡'
</script>

<style scoped>
/* ============================================
   CSS 变量系统 - 一致的设计语言
   ============================================ */
.structured-message {
  --primary-blue: #2b6fff;
  --primary-blue-light: #e8f0ff;
  --text-primary: #1a1a1a;
  --text-secondary: #6b7280;
  --text-tertiary: #9ca3af;
  --bg-surface: #ffffff;
  --bg-subtle: #f9fafb;
  --border-light: #e5e7eb;
  --border-medium: #d1d5db;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.04);
  --shadow-md: 0 4px 12px 0 rgb(0 0 0 / 0.06);
  --shadow-lg: 0 8px 24px 0 rgb(0 0 0 / 0.08);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;

  margin: 16px 0;
}

/* ============================================
   思考气泡 - 渐变、脉冲动画
   ============================================ */
.thought-bubble {
  background: linear-gradient(135deg, #f8f9ff 0%, #fff 100%);
  border: 1px solid rgba(43, 111, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  margin-bottom: 12px;
  animation: thoughtEnter 0.4s ease-out;
}

@keyframes thoughtEnter {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.thought-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background: var(--primary-blue);
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

.thought-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-blue);
  letter-spacing: 0.02em;
}

.thought-content {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* ============================================
   卡片容器
   ============================================ */
.cards-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-wrapper {
  animation: cardSlideIn 0.5s ease-out var(--delay) both;
}

@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: translateX(-12px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.card-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--bg-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  padding: 6px 10px;
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.tag-icon {
  font-size: 14px;
}

.tag-text {
  font-weight: 500;
}

/* ============================================
   总结容器
   ============================================ */
.summary-container {
  background: linear-gradient(180deg, #f0fdf4 0%, #ffffff 100%);
  border: 1px solid rgba(34, 197, 94, 0.15);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.success-icon-wrapper {
  width: 32px;
  height: 32px;
  background: #22c55e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.summary-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.summary-conclusion {
  margin: 0 0 16px;
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-primary);
}

.tools-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.tools-label {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.tools-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tool-pill {
  background: white;
  border: 1px solid var(--border-light);
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.summary-cards {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ============================================
   错误容器
   ============================================ */
.error-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #fef2f2;
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: var(--radius-md);
  padding: 14px 16px;
}

.error-icon-wrapper {
  width: 24px;
  height: 24px;
  background: #ef4444;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.error-text {
  margin: 0;
  font-size: 14px;
  color: #dc2626;
  line-height: 1.5;
}

/* ============================================
   过渡动画
   ============================================ */
.thought-fade-enter-active {
  transition: all 0.3s ease-out;
}

.thought-fade-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.card-slide-enter-active {
  transition: all 0.4s ease-out;
}

.card-slide-enter-from {
  opacity: 0;
  transform: translateX(-12px);
}

.summary-pop-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.summary-pop-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.error-shake-enter-active {
  transition: all 0.3s ease-out;
}

.error-shake-enter-from {
  opacity: 0;
  transform: translateX(-8px);
}
</style>
