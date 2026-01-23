import axios from "@/axios";

// 获取标签分页数据
export function getTagPageList(data) {
    return axios.post("/article/tag/list", data)
}

// 添加标签
export function addTag(data) {
    return axios.post("/article/tag/add", data)
}

// 删除标签
export function deleteTag(id) {
    return axios.post("/article/tag/delete", {id})
}

// 根据标签名模糊查询
export function searchTags(key) {
    return axios.post("/article/tag/search", {key})
}

// 获取标签 select 列表数据
export function getTagSelectList() {
    return axios.post("/article/tag/select/list")
}