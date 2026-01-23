package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.biz.domain.mapper.CategoryMapper;
import com.xzf.blog.article.biz.domain.mapper.StatisticsArticlePVDOMapper;
import com.xzf.blog.article.biz.domain.mapper.TagMapper;
import com.xzf.blog.article.biz.service.DashboardService;
import com.xzf.blog.article.biz.util.DateUtil;
import com.xzf.blog.article.dto.request.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xzf.blog.article.dto.request.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private StatisticsArticlePVDOMapper statisticsArticlePVDOMapper;

    @Override
    public Response<FindDashboardStatisticsInfoRspVO> findDashboardStatistics() {
        Long userId = LoginUserContextHolder.getUserId();
        // 查询文章总数
        Long articleTotalCount = articleMapper.selectCount(Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getAuthorId, userId));

        // 查询分类总数
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // 查询标签总数
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // 总浏览量
        List<ArticleDO> articleDOS = articleMapper.selectAllViewCount(userId);
        Long pvTotalCount = 0L;

        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 所有 read_num 相加
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getViewCount).sum();
        }

        // 组装 VO 类
        FindDashboardStatisticsInfoRspVO vo = FindDashboardStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build();

        return Response.success(vo);
    }

    /**
     * 获取文章这一年内每天的发布数量（如果发布数为0则不包含）
     *
     * @return
     */
    @Override
    public Response<Map<String, Long>> findDashboardPublishArticleStatistics() {

        Long userId = LoginUserContextHolder.getUserId();
        // 查询过去一年内的文章发布统计
        List<Map<String, Object>> rawData = articleMapper.selectPublishArticleStatisticsLastYearRaw(
                com.xzf.blog.article.enums.ArticleStatusEnum.ENABLE.getCode(), userId);

        // 转换数据格式：String日期 -> LocalDate, Long数量
        Map<String, Long> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map<String, Object> item : rawData) {
            String date = DateUtil.convertToLocalDate(item.get("publish_date")).format(formatter);
            Long count = ((Number) item.get("article_count")).longValue();
            result.put(date, count);
        }

        return Response.success(result);
    }

    /**
     * 获取文章最近一周 PV 访问量统计信息
     *
     * @return
     */
    @Override
    public Response<FindDashboardPVStatisticsInfoRspVO> findDashboardPVStatistics() {

        Long userId = LoginUserContextHolder.getUserId();
        // 查询最近一周的PV统计数据
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1);

        List<Map<String, Object>> rawData = statisticsArticlePVDOMapper.selectPVStatisticsByDateRange(oneWeekAgo, today, userId);

        // 转换数据格式
        List<String> pvDates = new ArrayList<>();
        List<Long> pvCounts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 创建一个Map来存储查询到的数据，方便查找
        Map<LocalDate, Long> pvDataMap = new LinkedHashMap<>();
        for (Map<String, Object> item : rawData) {
            LocalDate date = DateUtil.convertToLocalDate(item.get("pv_date"));
            Long count = item.get("pv_count") != null ? ((Number) item.get("pv_count")).longValue() : 0L;
            pvDataMap.put(date, count);
        }

        // 填充最近7天的完整数据，如果某天没有数据则为0
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            pvDates.add(date.format(formatter));
            pvCounts.add(pvDataMap.getOrDefault(date, 0L));
        }

        // 组装VO
        FindDashboardPVStatisticsInfoRspVO vo = FindDashboardPVStatisticsInfoRspVO.builder()
                .pvDates(pvDates)
                .pvCounts(pvCounts)
                .build();

        return Response.success(vo);
    }
}
