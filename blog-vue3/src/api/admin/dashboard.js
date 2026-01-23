import axios from "@/axios";

// 获取仪表盘基础信息（文章数、分类数、标签数、总浏览量）
export function getBaseStatisticsInfo(data) {
    return axios.post("/article/dashboard/statistics", data)
}

// 获取仪表盘文章发布热点统计信息
export function getPublishArticleStatisticsInfo(data) {
    return axios.post("/article/dashboard/publishArticle/statistics", data)
}

// 获取仪表盘最近一周 PV 访问量信息
export function getArticlePVStatisticsInfo(data) {
    return axios.post("/article/dashboard/pv/statistics", data)
}

