<script setup>
</script>

<template>
  <router-view />
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 16px;
  /* 全局背景改为深色，与新主题一致 */
  background-color: #020817;
  color: #e2e8f0;
  width: 100%;
  height: 100%;
  min-height: 100%;
  overflow-x: hidden;
}

#app {
  width: 100%;
  height: 100%;
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
}

/* 响应式字体大小 */
@media (max-width: 768px) {
  html, body { font-size: 15px; }
}

@media (max-width: 480px) {
  html, body { font-size: 14px; }
}

/* 深色主题全局滚动条 */
::-webkit-scrollbar { width: 4px; height: 4px; }

::-webkit-scrollbar-track { background: transparent; }

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.12);
  border-radius: 2px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.22);
}
</style>
