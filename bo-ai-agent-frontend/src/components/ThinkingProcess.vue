<template>
  <div class="thinking-process">
    <!-- Summary result area (shown above thinking process) -->
    <div v-if="summary" class="summary-section">
      <!-- PDF download button -->
      <div v-if="pdfUrl" class="pdf-download">
        <a :href="pdfUrl" target="_blank" download class="download-btn" @click.prevent="downloadPdf">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="7 10 12 15 17 10"/>
            <line x1="12" y1="15" x2="12" y2="3"/>
          </svg>
          下载报告
        </a>
      </div>
      <div class="summary-conclusion">{{ summary.conclusion }}</div>
      <div v-if="summary.cards?.length" class="summary-cards">
        <InfoCard
          v-for="(card, idx) in summary.cards"
          :key="`summary-card-${idx}`"
          :card="card"
          :compact="true"
        />
      </div>
    </div>

    <!-- Streaming summary text (chat-like) -->
    <div v-if="summaryText" class="streaming-summary">
      <div class="streaming-summary-text">{{ summaryText }}<span v-if="!isDone" class="typing-cursor">|</span></div>
    </div>

    <!-- Collapsible thinking process -->
    <div class="thinking-toggle" @click="toggleExpand">
      <div class="toggle-left">
        <svg v-if="!isDone" class="spinner-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"/>
        </svg>
        <svg v-else class="check-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M20 6L9 17l-5-5"/>
        </svg>
        <span class="toggle-label">{{ statusText }}</span>
      </div>
      <svg class="expand-arrow" :class="{ rotated: isExpanded }" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="6 9 12 15 18 9"/>
      </svg>
    </div>

    <!-- Expanded content -->
    <Transition name="collapse">
      <div v-if="isExpanded" class="thinking-content">
        <div class="steps-timeline">
          <div
            v-for="(step, idx) in displaySteps"
            :key="`step-${idx}`"
            class="timeline-step"
            :class="{ 'step-done': step.done }"
          >
            <div class="step-dot"></div>
            <span class="step-text">{{ step.label }}</span>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import InfoCard from './InfoCard.vue'

const props = defineProps({
  steps: {
    type: Array,
    default: () => []
  },
  toolResults: {
    type: Array,
    default: () => []
  },
  summary: {
    type: Object,
    default: null
  },
  summaryText: {
    type: String,
    default: ''
  },
  isDone: {
    type: Boolean,
    default: false
  },
  elapsedTime: {
    type: Number,
    default: 0
  }
})

const isExpanded = ref(false)

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

const statusText = computed(() => {
  if (props.isDone) {
    const sec = props.elapsedTime > 0 ? ` (${props.elapsedTime}s)` : ''
    return `已思考${sec}`
  }
  return '思考中...'
})

// Tool name -> natural language mapping
const toolDescriptions = {
  webSearchAdvanced: '正在搜索相关信息',
  webSearch: '正在搜索信息',
  search: '正在搜索',
  webScraping: '正在抓取网页内容',
  resourceDownload: '正在下载资源',
  writeFile: '正在整理结果',
  generatePDF: '正在生成报告',
  imageSearch: '正在搜索图片',
  doTerminate: '正在总结'
}

const getToolLabel = (toolName) => {
  return toolDescriptions[toolName] || '正在处理'
}

// Extract PDF download URL from generatePDF tool results
const pdfUrl = computed(() => {
  for (const action of props.toolResults) {
    const items = Array.isArray(action) ? action : action?.toolName ? [action] : []
    for (const item of items) {
      if (item.toolName === 'generatePDF') {
        // Support both `result` and `rawText` fields, strip surrounding quotes
        const text = (item.result || item.rawText || '').replace(/^["']|["']$/g, '')
        const match = text.match(/Download URL:\s*(\/api\/ai\/files\/\S+)/)
        if (match) {
          // Prefix with API base URL for correct resolution
          const apiBase = process.env.NODE_ENV === 'production'
            ? ''
            : `${window.location.protocol}//${window.location.hostname}:8123`
          return apiBase + match[1]
        }
      }
    }
  }
  return null
})

// Trigger file download via a hidden link
const downloadPdf = () => {
  if (!pdfUrl.value) return
  const link = document.createElement('a')
  link.href = pdfUrl.value
  link.target = '_blank'
  link.download = ''
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// Build merged display steps from thoughts + actions
const displaySteps = computed(() => {
  const result = []
  const seenTools = new Set()

  // Add action steps
  for (const action of props.toolResults) {
    if (Array.isArray(action)) {
      for (const item of action) {
        const toolName = item.toolName
        if (toolName && !seenTools.has(toolName)) {
          seenTools.add(toolName)
          result.push({
            label: toolName === 'generatePDF' ? '报告已生成' : getToolLabel(toolName),
            done: true
          })
        }
      }
    } else if (action?.toolName) {
      const toolName = action.toolName
      if (!seenTools.has(toolName)) {
        seenTools.add(toolName)
        result.push({
          label: toolName === 'generatePDF' ? '报告已生成' : getToolLabel(toolName),
          done: true
        })
      }
    }
  }

  // Limit to 5 steps
  if (result.length > 5) {
    return result.slice(0, 5)
  }

  return result
})

// Auto-expand during thinking, collapse when done
watch(() => props.isDone, (done) => {
  if (done) {
    // Collapse when finished
    isExpanded.value = false
  }
})
</script>

<style scoped>
.thinking-process {
  margin: 12px 0;
}

/* ============================================
   Summary section
   ============================================ */
.summary-section {
  margin-bottom: 12px;
}

.summary-conclusion {
  font-size: 15px;
  line-height: 1.7;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.summary-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ============================================
   Streaming summary text
   ============================================ */
.streaming-summary {
  margin-bottom: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #f0fdfa 0%, #ecfdf5 100%);
  border: 1px solid #d1fae5;
  border-radius: 12px;
}

.streaming-summary-text {
  font-size: 15px;
  line-height: 1.8;
  color: #1a1a1a;
  white-space: pre-wrap;
  word-break: break-word;
}

.typing-cursor {
  display: inline-block;
  color: #0d9488;
  font-weight: 300;
  animation: blink 0.8s step-end infinite;
  margin-left: 1px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* ============================================
   PDF download button
   ============================================ */
.pdf-download {
  margin-bottom: 12px;
}

.download-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #2563eb;
  color: #fff;
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.2s ease;
}

.download-btn:hover {
  background: #1d4ed8;
}

/* ============================================
   Collapsible toggle bar
   ============================================ */
.thinking-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f7f7f8;
  border-radius: 10px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s ease;
  border: 1px solid #e5e7eb;
}

.thinking-toggle:hover {
  background: #f0f0f2;
}

.toggle-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.spinner-icon {
  color: #6b7280;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.check-icon {
  color: #16a34a;
}

.toggle-label {
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
}

.expand-arrow {
  color: #9ca3af;
  transition: transform 0.2s ease;
  flex-shrink: 0;
}

.expand-arrow.rotated {
  transform: rotate(180deg);
}

/* ============================================
   Collapsible content
   ============================================ */
.thinking-content {
  padding: 12px 16px;
  background: #fafafa;
  border: 1px solid #e5e7eb;
  border-top: none;
  border-radius: 0 0 10px 10px;
}

.steps-timeline {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.timeline-step {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #6b7280;
}

.step-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d1d5db;
  flex-shrink: 0;
}

.timeline-step.step-done .step-dot {
  background: #16a34a;
}

.step-text {
  line-height: 1.4;
}

/* ============================================
   Collapse transition
   ============================================ */
.collapse-enter-active {
  transition: all 0.25s ease-out;
}

.collapse-leave-active {
  transition: all 0.2s ease-in;
}

.collapse-enter-from,
.collapse-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
