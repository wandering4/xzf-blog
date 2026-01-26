import axios from "@/axios";

// 获取评论分页数据
export function getCommentPageList(data) {
    return axios.post("/comment/list", data)
}

// 删除评论
export function deleteComment(id) {
    return axios.post("/comment/delete", {id})
}

