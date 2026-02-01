import axios from "@/axios";

// 获取对话历史分页数据
export function getArticlePageList(current,size,conversationKey) {
    return axios.post("/ai/chat/history/get", {current,size,conversationKey})
}

// 流式智能对话
export function getPersonalArticlePageList(articleId,conversationKey,message) {
    return axios.post("/ai/chat/stream/chat", {articleId,conversationKey,message} )
}