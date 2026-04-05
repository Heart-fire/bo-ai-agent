/**
 * 结构化消息类型定义
 * 对应后端新的卡片式数据结构
 */

// 消息类型枚举
export const MessageType = {
  STEP: 'STEP',
  TOOL_RESULT: 'TOOL_RESULT',
  SUMMARY: 'SUMMARY',
  SUMMARY_STREAM: 'SUMMARY_STREAM',
  SUMMARY_DONE: 'SUMMARY_DONE',
  ERROR: 'ERROR',
  THINKING_PROCESS: 'THINKING_PROCESS'
}

/**
 * @typedef {Object} ImageInfo
 * @property {string} url - 图片URL
 * @property {string} description - 图片描述
 */

/**
 * @typedef {Object} LinkInfo
 * @property {string} url - 链接URL
 * @property {string} title - 链接标题
 */

/**
 * @typedef {Object} Card
 * @property {string} title - 标题
 * @property {string} description - 描述
 * @property {string} [address] - 地址
 * @property {ImageInfo[]} [images] - 图片列表
 * @property {LinkInfo[]} [links] - 链接列表
 */

/**
 * @typedef {Object} ToolResultInfo
 * @property {string} toolName - 工具名称 (如: webSearch, webScraping, resourceDownload)
 * @property {Card} card - 卡片数据
 * @property {string} [result] - 原始结果文本
 */

/**
 * @typedef {Object} SummaryInfo
 * @property {string} type - 类型: "summary"
 * @property {number} totalCards - 总卡片数
 * @property {string[]} toolsUsed - 使用的工具列表
 * @property {string} conclusion - 结论
 * @property {Card[]} cards - 所有卡片列表
 */

/**
 * @typedef {Object} StepInfo
 * @property {string} type - 类型: "step"
 * @property {number} number - 步骤号
 * @property {string} thought - AI 思考内容
 * @property {string} [toolName] - 准备执行的工具名称
 */

/**
 * @typedef {Object} ErrorInfo
 * @property {string} type - 类型: "error"
 * @property {string} message - 错误信息
 */

/**
 * 解析SSE消息数据
 * @param {string} data - SSE接收到的原始数据
 * @returns {Object|Object[]|string} - 解析后的消息或原始字符串
 */
export function parseMessage(data) {
  if (!data || data === '[DONE]') {
    return data
  }

  try {
    const parsed = JSON.parse(data)

    // 1. 处理 thought 类型（思考步骤）
    if (parsed.type === 'thought' && parsed.data) {
      return {
        messageType: MessageType.STEP,
        data: {
          type: 'step',
          number: parsed.data.step,
          thought: parsed.data.content,
          plannedTool: parsed.data.plannedTool
        }
      }
    }

    // 2. 处理 step 类型（兼容 StructuredMessage 包装格式）
    if (parsed.type === 'step' && parsed.data) {
      const innerData = parsed.data
      // 提取 step 信息
      if (innerData.step) {
        const stepInfo = innerData.step
        return {
          messageType: MessageType.STEP,
          data: {
            type: 'step',
            number: stepInfo.number,
            thought: stepInfo.thought,
            plannedTool: stepInfo.plannedTool
          }
        }
      }
      // 如果没有 step 信息，返回 null（忽略）
      return null
    }

    // 3. 处理 action 类型（工具执行结果）
    if (parsed.type === 'action' && Array.isArray(parsed.data)) {
      return { messageType: MessageType.TOOL_RESULT, data: parsed.data }
    }

    // 4. 处理总结类型
    if (parsed.type === 'summary') {
      return { messageType: MessageType.SUMMARY, data: parsed }
    }

    // 4.1 处理流式总结文本块
    if (parsed.type === 'summary_stream' && parsed.content) {
      return { messageType: MessageType.SUMMARY_STREAM, data: parsed }
    }

    // 4.2 处理流式总结完成标记
    if (parsed.type === 'summary_done') {
      return { messageType: MessageType.SUMMARY_DONE, data: parsed }
    }

    // 5. 处理错误类型
    if (parsed.type === 'error') {
      return { messageType: MessageType.ERROR, data: parsed }
    }

    // 6. 处理数组格式（兼容旧格式）
    if (Array.isArray(parsed)) {
      return { messageType: MessageType.TOOL_RESULT, data: parsed }
    }

    // 7. 处理单个工具结果（兼容旧格式）
    if (parsed.toolName && parsed.card) {
      return { messageType: MessageType.TOOL_RESULT, data: [parsed] }
    }

    // 8. 处理直接的 thought 字段（兼容旧格式，返回 null 不展示）
    if (parsed.thought && !parsed.type) {
      return null
    }

    // 不是已知结构，返回原始文本
    return data
  } catch (e) {
    // JSON解析失败，返回原始文本
    return data
  }
}

/**
 * 从消息列表中提取完整的思考过程
 * @param {Array} messages - 消息列表
 * @returns {Object|null} - 思考过程对象或null
 */
export function extractThinkingProcess(messages) {
  const steps = []
  const toolResults = []
  let summary = null
  let error = null

  for (const msg of messages) {
    if (!msg.isStructured) continue

    if (msg.type === MessageType.STEP && msg.content?.data) {
      steps.push(msg.content.data)
    } else if (msg.type === MessageType.TOOL_RESULT && msg.content?.data) {
      toolResults.push(...msg.content.data)
    } else if (msg.type === MessageType.SUMMARY && msg.content?.data) {
      summary = msg.content.data
    } else if (msg.type === MessageType.ERROR && msg.content?.data) {
      error = msg.content.data
    }
  }

  if (steps.length === 0 && toolResults.length === 0 && !summary) {
    return null
  }

  return {
    steps,
    toolResults,
    summary,
    error
  }
}
