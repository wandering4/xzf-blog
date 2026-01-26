import axios from "@/axios";

// 获取文章分页数据
export function getPictureList(params = {}) {
    return axios.post("/article//blog/settings/advertisement/picture/list", params)
}