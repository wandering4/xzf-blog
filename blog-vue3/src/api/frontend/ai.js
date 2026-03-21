import axios from "@/axios";

// 获取对话历史分页数据
export function getArticlePageList(current,size,conversationKey) {
    return axios.post("/ai/chat/history/get", {current,size,conversationKey})
}

// 流式智能对话（RAG 模式）
export function getPersonalArticlePageList(articleId,conversationKey,message) {
    return axios.post("/ai/chat/stream/chat", {articleId,conversationKey,message} )
}

// Agent 智能对话端点路径（用于 fetch SSE 调用）
export const AGENT_CHAT_URL = '/api/ai/agent/chat/stream'