<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-section">
        <div class="footer-logo">
          <h3>北京政策智能助手平台</h3>
        </div>
        <div class="footer-links">
          <a href="#">《用户协议》</a>
          <a href="#">《隐私政策》</a>
        </div>
      </div>
      
      <div class="footer-section">
        <h4>友情链接</h4>
        <div class="footer-links">
          <a href="#" target="_blank">XXXX</a>
          <a href="#" target="_blank">XXXX</a>
        </div>
      </div>
      
      <div class="footer-section">
        <h4>联系我们</h4>
        <div class="footer-links">
          <a href="#">商务合作</a>
          <a href="#">站长：心火</a>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom">
      <p>© {{ currentYear }} 北京政策智能助手平台 - AI 让政策更简单</p>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

// 计算当前年份
const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  padding: 40px 0 20px;
  color: #ffffff;
  background-color: #0d0d0d;
  background-image: url(https://cdn.bigmodel.cn/static/platform/images/footer/footer-banner-left.gif), url(https://cdn.bigmodel.cn/static/platform/images/footer/footer-banner-right.gif);
  background-repeat: no-repeat, no-repeat;
  background-position: -56px 90%, 104% -56%;
  background-size: auto 68%, auto 74%;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  width: 100%;
  margin-top: auto;
  position: relative;
  z-index: 2;
  box-sizing: border-box;
  left: 0;
  right: 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
}

.footer-section {
  flex: 1;
  min-width: 200px;
  margin-bottom: 30px;
  padding: 0 15px;
}

.footer-logo h3 {
  font-size: 1.25rem;
  margin-bottom: 15px;
  color: #ffffff;
  font-weight: 600;
}

.footer-section h4 {
  font-size: 1rem;
  margin-bottom: 15px;
  color: #ffffff;
  font-weight: 600;
}

.footer-links {
  display: flex;
  flex-direction: column;
}

.footer-links a {
  margin-bottom: 10px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: color 0.2s;
  font-size: 0.95rem;
}

.footer-links a:hover {
  color: #ffffff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
  }

  .footer-section {
    width: 100%;
    margin-bottom: 20px;
    padding: 0;
  }
}

@media (max-width: 480px) {
  .app-footer {
    padding: 30px 0 15px;
  }

  .footer-section h4 {
    font-size: 0.95rem;
  }

  .footer-links a {
    font-size: 0.9rem;
  }

  .qrcode-placeholder {
    width: 80px;
    height: 80px;
  }
}
</style> 