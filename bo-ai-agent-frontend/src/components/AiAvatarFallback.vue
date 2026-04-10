<template>
  <div class="ai-avatar-fallback" :class="type">
    <span v-if="type === 'policy'">📋</span>
    <span v-else-if="type === 'research'">🔬</span>
    <span v-else>🤖</span>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'default'
  }
})
</script>

<style scoped>
.ai-avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  border-radius: 50%;
}
</style> 