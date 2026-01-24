package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.response.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xzf.blog.article.dto.response.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xzf.blog.framework.commons.response.Response;

import java.util.Map;

public interface DashboardService {
    /**
     * 获取仪表盘基础统计信息
     * @return
     */
    Response<FindDashboardStatisticsInfoRspVO> findDashboardStatistics();

    /**
     * 获取文章发布热点统计信息
     * @return
     */
    Response<Map<String, Long>> findDashboardPublishArticleStatistics();

    /**
     * 获取文章最近一周 PV 访问量统计信息
     * @return
     */
    Response<FindDashboardPVStatisticsInfoRspVO> findDashboardPVStatistics();

}
