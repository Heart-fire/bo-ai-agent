<p align="center">
  <img src="https://xinhuo-store.oss-cn-hangzhou.aliyuncs.com/%E4%B8%BB%E9%A1%B5.jpg" alt="Vibe Coding" width="600"/>
</p>
<h1 align="center">政策通</h1>
<p align="center">
  <b>完全免费政策问答平台 | 减少信息差，让每个人都能享受技术红利</b>
</p>
**政策通 Agent** 是一款面向北京政务场景的智能 AI 助手系统，基于 Spring Boot 3.4 + Spring AI 框架构建后端服务，配合 Vue 3 前端实现全链路交互。系统提供两大核心能力：**政策问答**——通过 RAG（检索增强生成）技术对接 DashScope 云端知识库，结合查询重写、多轮会话记忆与 SSE 流式输出，为用户提供精准的北京社保、交通、政务办事等政策解答；**研究 Agent**——基于 ReAct 推理范式的自主智能体，能够自主规划搜索策略、抓取网页内容、整理信息并生成专业 PDF 调研报告。在安全层面，系统实现了三层递进式输入防护（关键词黑名单 → AI 意图识别 → 多模型交叉验证），有效抵御提示词注入等攻击。底层采用 PostgreSQL + Redis 混合存储会话记忆，支持 DashScope、DeepSeek、智谱 BigModel 等多模型动态路由，并通过 MCP 协议扩展图片搜索等工具能力，整体通过 Docker Compose 一键编排部署。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.4, Spring AI, JDK 21 |
| AI 模型 | DashScope (Qwen3), DeepSeek, 智谱 BigModel |
| 前端框架 | Vue 3, Vue Router, Vite, Marked |
| 数据存储 | PostgreSQL (PgVector), Redis |
| 安全防护 | 三层输入安全防御 (关键词 + 意图识别 + 交叉验证) |
| 部署方案 | Docker Compose, Nginx 反向代理 |


## 核心功能

- **政策智能问答** — RAG 检索增强 + 知识库召回，支持多轮对话与流式输出
- **自主研究 Agent** — ReAct 推理范式，自动搜索、抓取、整理并生成 PDF 报告
- **三层安全防御** — 关键词黑名单 → AI 意图识别 → 多模型交叉验证，阻断提示词注入
- **多模型动态路由** — 支持 DashScope / DeepSeek / BigModel 热切换
- **混合会话记忆** — Redis 热缓存 + PostgreSQL 持久化，Kryo 高效序列化
- **MCP 工具扩展** — 通过 Model Context Protocol 协议扩展图片搜索等能力

