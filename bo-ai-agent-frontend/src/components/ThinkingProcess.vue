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
            <span class="step-status">调用成功</span>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 2. 最终结果：流式摘要文本（Markdown 渲染） -->
    <div v-if="summaryText" class="streaming-summary">
      <div class="streaming-summary-text" v-html="renderMd(summaryText)"></div>
      <span v-if="!isDone" class="typing-cursor">|</span>
    </div>

    <!-- 3. 最终结果：结构化摘要（结论 + 信息卡片 + 参考链接 + PDF 下载） -->
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
      <!-- 参考链接汇总 -->
      <div v-if="referenceLinks.length" class="reference-links">
        <div class="ref-links-title">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
          </svg>
          参考链接
        </div>
        <div class="ref-links-list">
          <a
            v-for="(link, idx) in referenceLinks"
            :key="`ref-${idx}`"
            :href="link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="ref-link-item"
          >
            <span class="ref-link-index">{{ idx + 1 }}</span>
            <span class="ref-link-title">{{ link.title }}</span>
            <span class="ref-link-url">{{ link.url }}</span>
            <svg class="ref-link-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="7" y1="17" x2="17" y2="7"/>
              <polyline points="7 7 17 7 17 17"/>
            </svg>
          </a>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'
import InfoCard from './InfoCard.vue'

// ── 配置 marked ────────────────────────────────────────────
marked.setOptions({
  breaks: true,
  gfm: true,
})

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

// Tool name -> display label mapping
const toolLabels = {
  webSearchAdvanced: '调用政策通网页搜索工具',
  webSearch: '搜索',
  search: '搜索',
  webScraping: '网页抓取',
  resourceDownload: '资源下载',
  writeFile: '整理结果',
  generatePDF: '生成报告',
  imageSearch: '图片搜索',
  doTerminate: '已完成'
}

const getToolLabel = (toolName) => {
  return toolLabels[toolName] || toolName
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

// Collect all unique reference links from summary cards
const referenceLinks = computed(() => {
  if (!props.summary?.cards) return []
  const seen = new Set()
  const links = []
  for (const card of props.summary.cards) {
    if (card.links?.length) {
      for (const link of card.links) {
        if (link.url && !seen.has(link.url)) {
          seen.add(link.url)
          links.push({ url: link.url, title: link.title || card.title || '参考链接' })
        }
      }
    }
  }
  return links
})

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
            label: getToolLabel(toolName),
            toolName,
            done: true
          })
        }
      }
    } else if (action?.toolName) {
      const toolName = action.toolName
      if (!seenTools.has(toolName)) {
        seenTools.add(toolName)
        result.push({
          label: getToolLabel(toolName),
          toolName,
          done: true
        })
      }
    }
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

// ── Markdown 预处理（修复 AI 输出的常见格式问题）─────────
const preprocessMarkdown = (text) => {
  if (!text) return ''
  let md = text
  // 修复错位的粗体标记：*文本** → **文本**
  md = md.replace(/^\*([^*\n]+\S)\*\*\s*$/gm, '**$1**')
  // 修复行首缺少空格的列表/标题/引用标记
  md = md.replace(/^([-*])\s*(?=[^\s])/gm, '$1 ')
  md = md.replace(/^(\d+\.)\s*(?=[^\s\d])/gm, '$1 ')
  md = md.replace(/^(#{1,6})\s*(?=[^\s#])/gm, '$1 ')
  md = md.replace(/^>\s*(?=[^\s>])/gm, '> ')
  return md
}

// ── Markdown 渲染（使用 marked）──────────────────────────────
const renderMd = (text) => {
  if (!text) return ''
  try {
    return marked.parse(preprocessMarkdown(text))
  } catch {
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\n/g, '<br/>')
  }
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
   Reference links section
   ============================================ */
.reference-links {
  margin-top: 16px;
  padding: 14px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

.ref-links-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 10px;
}

.ref-links-title svg {
  color: #6366f1;
}

.ref-links-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.ref-link-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.15s ease;
}

.ref-link-item:hover {
  border-color: #a5b4fc;
  background: #eff6ff;
  transform: translateX(2px);
}

.ref-link-index {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #6366f1;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ref-link-title {
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 280px;
}

.ref-link-url {
  flex: 1;
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ref-link-arrow {
  color: #94a3b8;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.15s ease;
}

.ref-link-item:hover .ref-link-arrow {
  opacity: 1;
  color: #6366f1;
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
  word-break: break-word;
}

.streaming-summary-text :deep(strong) {
  font-weight: 700;
  color: #0f172a;
}

.streaming-summary-text :deep(h1) {
  font-size: 1.15rem; font-weight: 700; color: #0f172a; margin: 12px 0 6px;
}
.streaming-summary-text :deep(h2) {
  font-size: 1rem; font-weight: 600; color: #1e293b; margin: 10px 0 6px;
}
.streaming-summary-text :deep(h3) {
  font-size: 0.92rem; font-weight: 600; color: #334155; margin: 8px 0 4px;
}
.streaming-summary-text :deep(h4) {
  font-size: 0.88rem; font-weight: 600; color: #475569; margin: 6px 0 3px;
}
.streaming-summary-text :deep(p) {
  margin: 4px 0; color: #475569; font-size: 0.9rem; line-height: 1.7;
}
.streaming-summary-text :deep(blockquote) {
  margin: 8px 0; padding: 8px 12px;
  border-left: 3px solid #0d9488;
  background: rgba(13,148,136,0.06);
  border-radius: 0 8px 8px 0;
  color: #475569; font-size: 0.87rem;
}
.streaming-summary-text :deep(blockquote p) { color: inherit; font-size: inherit; margin: 0; }
.streaming-summary-text :deep(code) {
  padding: 2px 5px; border-radius: 4px;
  background: rgba(13,148,136,0.08); color: #0d9488;
  font-size: 0.85rem; font-family: 'Fira Code', Consolas, monospace;
}
.streaming-summary-text :deep(pre) {
  margin: 8px 0; padding: 12px; border-radius: 8px;
  background: #f8fafc; border: 1px solid #e5e7eb;
  overflow-x: auto; line-height: 1.5;
  font-size: 0.82rem; font-family: 'Fira Code', Consolas, monospace;
}
.streaming-summary-text :deep(pre code) {
  padding: 0; border: none; background: none; color: #334155; font-size: inherit;
}
.streaming-summary-text :deep(ul),
.streaming-summary-text :deep(ol) { margin: 4px 0; padding-left: 18px; }
.streaming-summary-text :deep(li) { color: #475569; font-size: 0.9rem; line-height: 1.7; margin-bottom: 2px; }
.streaming-summary-text :deep(a) {
  color: #0d9488; text-decoration: none;
  border-bottom: 1px solid rgba(13,148,136,0.3);
  transition: border-color 0.15s, color 0.15s;
}
.streaming-summary-text :deep(a:hover) {
  color: #0f766e; border-bottom-color: #0f766e;
}
.streaming-summary-text :deep(hr) {
  border: none; border-top: 1px solid #e5e7eb; margin: 12px 0;
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
  margin-bottom: 16px;
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
  font-weight: 500;
  color: #374151;
}

.step-status {
  font-size: 12px;
  color: #16a34a;
  margin-left: auto;
  flex-shrink: 0;
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
