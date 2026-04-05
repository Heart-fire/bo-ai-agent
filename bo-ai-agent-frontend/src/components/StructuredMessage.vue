<template>
  <div class="structured-message">
    <!-- Agent response: ThinkingProcess + Summary -->
    <template v-if="message.messageType === 'agent-response'">
      <ThinkingProcess
        :steps="message.data.steps || []"
        :tool-results="message.data.toolResults || []"
        :summary="message.data.summary || null"
        :is-done="message.data.isDone || false"
        :elapsed-time="message.data.elapsedTime || 0"
      />
    </template>

    <!-- Legacy STEP: rendered inside ThinkingProcess now, hidden individually -->
    <template v-if="message.messageType === 'STEP'">
      <!-- Hidden: rendered in ThinkingProcess -->
    </template>

    <!-- Legacy TOOL_RESULT: hidden, rendered in ThinkingProcess -->
    <template v-if="message.messageType === 'TOOL_RESULT'">
      <!-- Hidden: rendered in ThinkingProcess -->
    </template>

    <!-- SUMMARY: legacy standalone summary -->
    <template v-else-if="message.messageType === 'SUMMARY'">
      <div class="summary-container">
        <div class="summary-header">
          <div class="success-icon-wrapper">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M20 6L9 17l-5-5"/>
            </svg>
          </div>
          <span class="summary-title">已完成</span>
        </div>
        <p class="summary-conclusion">{{ summaryData.conclusion }}</p>
        <div v-if="summaryData.cards?.length" class="summary-cards">
          <InfoCard
            v-for="(card, idx) in summaryData.cards"
            :key="`summary-${idx}`"
            :card="card"
            :compact="true"
          />
        </div>
      </div>
    </template>

    <!-- ERROR -->
    <template v-else-if="message.messageType === 'ERROR'">
      <div class="error-container">
        <div class="error-icon-wrapper">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 8v4M12 16h.01"/>
          </svg>
        </div>
        <p class="error-text">{{ errorData.message || '处理出错，请重试' }}</p>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import InfoCard from './InfoCard.vue'
import ThinkingProcess from './ThinkingProcess.vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const summaryData = computed(() => props.message.data || {})
const errorData = computed(() => props.message.data || {})
</script>

<style scoped>
.structured-message {
  margin: 12px 0;
}

/* Summary container */
.summary-container {
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.success-icon-wrapper {
  width: 24px;
  height: 24px;
  background: #22c55e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.summary-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
}

.summary-conclusion {
  margin: 0 0 12px;
  font-size: 15px;
  line-height: 1.6;
  color: #1a1a1a;
}

.summary-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* Error container */
.error-container {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  background: #fef2f2;
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 10px;
  padding: 12px 14px;
}

.error-icon-wrapper {
  width: 22px;
  height: 22px;
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
</style>
