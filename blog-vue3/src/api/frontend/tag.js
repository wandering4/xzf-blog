import axios from "@/axios";

// 获取标签列表
export function getTagList(data) {
    return axios.post("/article/tag/list", data)
}

// 获取标签下文章列表
export function getTagArticlePageList(data) {
    return axios.post("/article/list", data)
}


