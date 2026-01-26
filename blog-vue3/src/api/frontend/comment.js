import axios from "@/axios";

// 发布评论
export function publishComment(data) {
    return axios.post("/comment/publish", data)
}

// 获取评论分页列表
export function getCommentPageList(data) {
    return axios.post("/comment/list", data)
}

// 删除评论
export function deleteComment(id) {
    return axios.post("/comment/delete", { id })
}
