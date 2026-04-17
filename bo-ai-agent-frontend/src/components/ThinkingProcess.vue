<template>
  <div class="thinking-process">

    <!-- 1. 思考过程 + 查找结果（折叠/展开，合为一体） -->
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

    <!-- 查找结果步骤（在思考框内部展开） -->
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

    <!-- 2. 最终结果：流式摘要文本（Markdown 渲染） -->
    <div v-if="summaryText" class="streaming-summary">
      <div class="streaming-summary-text" v-html="renderMd(summaryText)"></div>
      <span v-if="!isDone" class="typing-cursor">|</span>
    </div>

    <!-- 3. 最终结果：结构化摘要（结论 + 信息卡片 + PDF 下载） -->
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

// 简易 Markdown 渲染（支持粗体、标题、列表、代码块、引用、换行）
const renderMd = (text) => {
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

  // 标题
  html = html.replace(/^### (.+)$/gm, '<h4 class="md-h3">$1</h4>')
  html = html.replace(/^## (.+)$/gm,  '<h3 class="md-h2">$1</h3>')
  html = html.replace(/^# (.+)$/gm,   '<h2 class="md-h1">$1</h2>')

  // 引用块
  html = html.replace(/^&gt; (.+)$/gm, '<blockquote class="md-blockquote">$1</blockquote>')

  // 无序列表
  html = html.replace(/^[-*•] (.+)$/gm, '<li class="md-li">$1</li>')
  html = html.replace(/(<li class="md-li">.*<\/li>\n?)+/g, m => `<ul class="md-ul">${m}</ul>`)

  // 有序列表
  html = html.replace(/^\d+\. (.+)$/gm, '<li class="md-oli">$1</li>')
  html = html.replace(/(<li class="md-oli">.*<\/li>\n?)+/g, m => `<ol class="md-ol">${m}</ol>`)

  // 粗体
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')

  // 内联代码
  html = html.replace(/`([^`]+)`/g, '<code class="md-inline-code">$1</code>')

  // 段落 & 换行
  html = html.replace(/\n\n/g, '</p><p>')
  html = html.replace(/\n/g, '<br/>')
  html = `<p>${html}</p>`

  // 清理被 p 包裹的块级元素
  html = html.replace(/<p><(h[2-4]|ul|ol|pre|blockquote)/g, '<$1')
  html = html.replace(/<\/(h[2-4]|ul|ol|pre|blockquote)><\/p>/g, '</$1>')

  return html
}
</script>

<style scoped>
.thinking-process {
  margin: 12px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ============================================
   Summary section
   ============================================ */
.summary-section {
  /* gap handles spacing */
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
  padding: 5px 5px;
  //background: linear-gradient(135deg, #ffffff 0%, #fbfffd 100%);
  //border: 1px solid #d1fae5;
  border-radius: 12px;
  line-height: 1.8;
}

.streaming-summary-text {
  font-size: 15px;
  line-height: 1.8;
  color: #1a1a1a;
  white-space: pre-wrap;
  word-break: break-word;
}

.streaming-summary-text :deep(strong) {
  font-weight: 700;
  color: #0f172a;
}

.streaming-summary-text :deep(.md-h1) {
  font-size: 1.15rem; font-weight: 700; color: #0f172a; margin: 12px 0 6px;
}
.streaming-summary-text :deep(.md-h2) {
  font-size: 1rem; font-weight: 600; color: #1e293b; margin: 10px 0 6px;
}
.streaming-summary-text :deep(.md-h3) {
  font-size: 0.92rem; font-weight: 600; color: #334155; margin: 8px 0 4px;
}
.streaming-summary-text :deep(.md-blockquote) {
  margin: 8px 0; padding: 8px 12px;
  border-left: 3px solid #0d9488;
  background: rgba(13,148,136,0.06);
  border-radius: 0 8px 8px 0;
  color: #475569; font-size: 0.87rem;
}
.streaming-summary-text :deep(.md-inline-code) {
  padding: 2px 5px; border-radius: 4px;
  background: rgba(13,148,136,0.08); color: #0d9488;
  font-size: 0.85rem; font-family: 'Fira Code', Consolas, monospace;
}
.streaming-summary-text :deep(.md-code) {
  margin: 8px 0; padding: 12px; border-radius: 8px;
  background: #f8fafc; border: 1px solid #e5e7eb;
  color: #334155; font-size: 0.82rem; overflow-x: auto; line-height: 1.5;
  font-family: 'Fira Code', Consolas, monospace;
}
.streaming-summary-text :deep(.md-ul),
.streaming-summary-text :deep(.md-ol) { margin: 4px 0; padding-left: 18px; }
.streaming-summary-text :deep(.md-li),
.streaming-summary-text :deep(.md-oli) { color: #475569; font-size: 0.9rem; line-height: 1.7; margin-bottom: 2px; }

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
  /* gap handles spacing */
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
