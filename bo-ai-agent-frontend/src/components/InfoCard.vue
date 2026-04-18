<template>
  <div class="info-card" :class="{ compact }">
    <!-- 卡片头部 -->
    <div class="card-header">
      <h4 class="card-title">
        <a v-if="card.links?.length === 1 && card.links[0].url"
           :href="card.links[0].url"
           target="_blank"
           rel="noopener noreferrer"
           class="card-title-link"
        >{{ card.title }}</a>
        <span v-else>{{ card.title }}</span>
      </h4>
      <button
        v-if="card.address"
        class="copy-btn"
        @click="copyAddress"
        :class="{ copied: copyState === 'copied' }"
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
        </svg>
      </button>
    </div>

    <!-- 描述内容 -->
    <p class="card-description">{{ card.description }}</p>

    <!-- 标签（来源、日期等） -->
    <div v-if="card.tags?.length" class="card-tags">
      <span v-for="(tag, idx) in card.tags" :key="idx" class="tag-item">{{ tag }}</span>
    </div>

    <!-- 地址信息 -->
    <div v-if="card.address" class="address-row">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
        <circle cx="12" cy="10" r="3"/>
      </svg>
      <span class="address-text">{{ card.address }}</span>
    </div>

    <!-- 图片网格 -->
    <div v-if="card.images?.length" class="images-grid">
      <div
        v-for="(img, idx) in card.images"
        :key="idx"
        class="image-item"
        @click="openImage(img.url)"
      >
        <img :src="img.url" :alt="img.description" loading="lazy" />
        <div v-if="img.description" class="image-overlay">
          <span class="image-caption">{{ img.description }}</span>
        </div>
      </div>
    </div>

    <!-- 链接列表 -->
    <div v-if="card.links?.length" class="links-list">
      <a
        v-for="(link, idx) in card.links"
        :key="idx"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-item"
      >
        <span class="link-icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
            <polyline points="15 3 21 3 21 9"/>
            <line x1="10" y1="14" x2="21" y2="3"/>
          </svg>
        </span>
        <span class="link-text">{{ link.title || link.url }}</span>
        <svg class="link-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="5" y1="12" x2="19" y2="12"/>
          <polyline points="12 5 19 12 12 19"/>
        </svg>
      </a>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  card: {
    type: Object,
    required: true
  },
  compact: {
    type: Boolean,
    default: false
  }
})

const copyState = ref('idle')

const copyAddress = async () => {
  if (props.card.address) {
    try {
      await navigator.clipboard.writeText(props.card.address)
      copyState.value = 'copied'
      setTimeout(() => {
        copyState.value = 'idle'
      }, 2000)
    } catch (err) {
      console.error('Copy failed:', err)
    }
  }
}

const openImage = (url) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
/* ============================================
   CSS 变量
   ============================================ */
.info-card {
  --card-bg: #ffffff;
  --card-border: #e5e7eb;
  --card-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.06), 0 1px 2px 0 rgb(0 0 0 / 0.04);
  --card-shadow-hover: 0 4px 12px 0 rgb(0 0 0 / 0.08), 0 2px 4px 0 rgb(0 0 0 / 0.04);
  --title-color: #111827;
  --text-color: #4b5563;
  --text-muted: #9ca3af;
  --accent-blue: #f8d1d1;
  --accent-blue-bg: #eff6ff;

  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 12px;
  padding: 16px;
  box-shadow: var(--card-shadow);
  transition: all 0.25s ease;
}

.info-card:hover {
  box-shadow: var(--card-shadow-hover);
  border-color: #d1d5db;
}

/* 紧凑模式 */
.info-card.compact {
  --card-bg: #f9fafb;
  --card-border: #e5e7eb;
  border-radius: 10px;
  padding: 14px;
}

.info-card.compact:hover {
  --card-bg: #f3f4f6;
}

/* ============================================
   卡片头部
   ============================================ */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--title-color);
  line-height: 1.4;
  flex: 1;
}

.info-card.compact .card-title {
  font-size: 14px;
}

.card-title-link {
  color: inherit;
  text-decoration: none;
  transition: color 0.15s ease;
}

.card-title-link:hover {
  color: #2563eb;
  text-decoration: underline;
}

.copy-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f3f4f6;
  border-radius: 8px;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.copy-btn:hover {
  background: var(--accent-blue-bg);
  color: var(--accent-blue);
}

.copy-btn.copied {
  background: #dcfce7;
  color: #16a34a;
}

/* ============================================
   描述文本
   ============================================ */
.card-description {
  margin: 0 0 12px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-color);
}

.info-card.compact .card-description {
  font-size: 13px;
  margin-bottom: 10px;
}

/* ============================================
   标签
   ============================================ */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tag-item {
  display: inline-block;
  padding: 2px 8px;
  background: #f0f9ff;
  color: #0369a1;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.5;
}

.info-card.compact .tag-item {
  font-size: 11px;
  padding: 1px 6px;
}

/* ============================================
   地址行
   ============================================ */
.address-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 12px;
}

.address-row svg {
  color: var(--text-muted);
  flex-shrink: 0;
}

.address-text {
  font-size: 13px;
  color: var(--text-color);
  word-break: break-word;
}

.info-card.compact .address-row {
  padding: 8px 10px;
}

/* ============================================
   图片网格
   ============================================ */
.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 8px;
  margin-top: 14px;
}

.info-card.compact .images-grid {
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 6px;
}

.image-item {
  position: relative;
  aspect-ratio: 16 / 10;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  background: #f3f4f6;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-item:hover img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px 10px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
}

.image-caption {
  font-size: 11px;
  color: white;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ============================================
   链接列表
   ============================================ */
.links-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 14px;
}

.link-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: var(--accent-blue-bg);
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s ease;
}

.link-item:hover {
  background: #dbeafe;
  transform: translateX(4px);
}

.link-icon {
  color: var(--accent-blue);
  flex-shrink: 0;
}

.link-text {
  flex: 1;
  font-size: 13px;
  color: var(--accent-blue);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.link-arrow {
  color: var(--accent-blue);
  opacity: 0;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.link-item:hover .link-arrow {
  opacity: 1;
}

.info-card.compact .link-item {
  padding: 8px 10px;
}

.info-card.compact .link-text {
  font-size: 12px;
}
</style>
