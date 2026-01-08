package com.xzf.blog.article.biz.service.impl;

import com.xzf.blog.article.biz.service.DashboardService;
import com.xzf.blog.article.dto.request.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xzf.blog.article.dto.request.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xzf.blog.framework.commons.response.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Override
    public Response<FindDashboardStatisticsInfoRspVO> findDashboardStatistics() {
        return null;
    }

    @Override
    public Response<Map<LocalDate, Long>> findDashboardPublishArticleStatistics() {
        return null;
    }

    @Override
    public Response<FindDashboardPVStatisticsInfoRspVO> findDashboardPVStatistics() {
        return null;
    }
}
