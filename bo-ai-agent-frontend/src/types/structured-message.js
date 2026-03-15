/**
 * 结构化消息类型定义
 * 对应后端新的卡片式数据结构
 */

// 消息类型枚举
export const MessageType = {
  STEP: 'STEP',
  TOOL_RESULT: 'TOOL_RESULT',
  SUMMARY: 'SUMMARY',
  ERROR: 'ERROR'
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

    // 判断消息类型
    if (parsed.type === 'summary') {
      // 总结类型
      return { messageType: MessageType.SUMMARY, data: parsed }
    } else if (parsed.type === 'error') {
      // 错误类型
      return { messageType: MessageType.ERROR, data: parsed }
    } else if (Array.isArray(parsed)) {
      // 数组格式：可能是 [思考结果, 卡片结果] 或 多个工具结果
      return { messageType: MessageType.TOOL_RESULT, data: parsed }
    } else if (parsed.toolName && parsed.card) {
      // 单个工具结果
      return { messageType: MessageType.TOOL_RESULT, data: [parsed] }
    } else if (parsed.thought) {
      // 单独的思考步骤（返回原始文本，不展示）
      return null
    }

    // 不是已知结构，返回原始文本
    return data
  } catch (e) {
    // JSON解析失败，返回原始文本
    return data
  }
}
