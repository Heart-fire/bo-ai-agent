import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      title: '首页 - 北京政策智能助手平台',
      description: '北京政策智能助手平台提供政策问答和信息采集研究员服务，帮您快速了解北京最新政策'
    }
  },
  {
    path: '/policy-chat',
    name: 'PolicyChat',
    component: () => import('../views/PolicyChat.vue'),
    meta: {
      title: '政策问答 - 北京政策智能助手平台',
      description: '基于RAG知识库的北京政策问答助手，快速解答社保、交通、积分落户等政策问题'
    }
  },
  {
    path: '/research-agent',
    name: 'ResearchAgent',
    component: () => import('../views/ResearchAgent.vue'),
    meta: {
      title: '信息采集研究员 - 北京政策智能助手平台',
      description: '自主采集公开信息，生成政策研究报告，支持积分落户、社保、交通等多类政策'
    }
  },
  {
    path: '/docs',
    name: 'Documentation',
    component: () => import('../views/Documentation.vue'),
    meta: {
      title: '文档 - 政策通',
      description: '政策通使用文档：快速开始、使用指南、API 文档和常见问题'
    }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue'),
    meta: {
      title: '关于 - 政策通',
      description: '了解政策通：AI 驱动的政策咨询平台，让每个人都能读懂政策'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫，设置文档标题
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router