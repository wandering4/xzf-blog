import axios from "@/axios";

// 获取文章分页数据
export function getArticlePageList(data) {
    return axios.post("/article/list", data)
}

// 获取个人主页文章分页数据
export function getPersonalArticlePageList(data) {
    return axios.post("/article/personal/list", data)
}

// 删除文章
export function deleteArticle(id) {
    return axios.post("/article/delete", {id})
}

// 发布文章
export function publishArticle(data) {
    return axios.post("/article/publish", data)
}

// 获取文章详情
export function getArticleDetail(id) {
    return axios.post("/article/detail", {id})
}

// 更新文章
export function updateArticle(data) {
    return axios.post("/article/update", data)
}

// 更新文章置顶状态
export function updateArticleIsTop(data) {
    return axios.post("/article/isTop/update", data)
}


