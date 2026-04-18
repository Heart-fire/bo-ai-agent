import axios from 'axios'

// 根据环境变量设置 API 基础 URL
const API_BASE_URL = process.env.NODE_ENV === 'production'
 ? '/api' // 生产环境使用相对路径
 : `${window.location.protocol}//${window.location.hostname}:8123/api` // 开发环境使用当前访问IP

// 创建axios实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000
})

// 封装SSE连接
export const connectSSE = (url, params, onMessage, onError) => {
  // 构建带参数的URL
  const queryString = Object.keys(params)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&')

  const fullUrl = `${API_BASE_URL}${url}?${queryString}`

  // 创建EventSource
  const eventSource = new EventSource(fullUrl)

  eventSource.onmessage = event => {
    let data = event.data

    // 检查是否是特殊标记
    if (data === '[DONE]') {
      if (onMessage) onMessage('[DONE]')
    } else {
      // 处理普通消息
      if (onMessage) onMessage(data)
    }
  }

  eventSource.onerror = error => {
    if (onError) onError(error)
    eventSource.close()
  }

  // 返回eventSource实例，以便后续可以关闭连接
  return eventSource
}

// 获取或生成会话级 chatId（存 sessionStorage，关闭标签页自动清除）
export const getPersistentChatId = () => {
  let chatId = sessionStorage.getItem('chatId')
  if (!chatId) {
    chatId = crypto.randomUUID ? crypto.randomUUID() : 'chat_' + Math.random().toString(36).substring(2, 18)
    sessionStorage.setItem('chatId', chatId)
  }
  return chatId
}

// 政策问答 SSE 流式对话
export const chatWithPolicyApp = (message, chatId, model) => {
  const params = { message, chatId }
  if (model) params.model = model
  return connectSSE('/ai/policy_app/chat/sse', params)
}

// 信息采集研究员 Agent SSE 流式
export const chatWithResearchAgent = (message, model) => {
  const params = { message }
  if (model) params.model = model
  return connectSSE('/ai/research/chat', params)
}

// 超级智能体 SSE 流式
export const chatWithManus = (message) => {
  return connectSSE('/ai/manus/chat', { message })
}

export default {
  chatWithPolicyApp,
  chatWithResearchAgent,
  chatWithManus,
  getPersistentChatId
}