package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.biz.service.DashboardService;
import com.xzf.blog.article.dto.response.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xzf.blog.article.dto.response.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /*==================================  管理系统接口  ====================================*/

    @PostMapping("/statistics")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    //    @PreAuthorize
    public Response<FindDashboardStatisticsInfoRspVO> findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    //    @PreAuthorize
    public Response<Map<String, Long>> findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @ApiOperationLog(description = "获取后台仪表盘最近一周 PV 访问量信息")
    //    @PreAuthorize
    public Response<FindDashboardPVStatisticsInfoRspVO> findDashboardPVStatistics() {
        return dashboardService.findDashboardPVStatistics();
    }

}
