package com.xzf.blog.article.biz.service.impl;

import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.biz.domain.mapper.CategoryMapper;
import com.xzf.blog.article.biz.domain.mapper.StatisticsArticlePVDOMapper;
import com.xzf.blog.article.biz.domain.mapper.TagMapper;
import com.xzf.blog.article.dto.response.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xzf.blog.article.dto.response.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private StatisticsArticlePVDOMapper statisticsArticlePVDOMapper;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl();
        ReflectionTestUtils.setField(dashboardService, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(dashboardService, "categoryMapper", categoryMapper);
        ReflectionTestUtils.setField(dashboardService, "tagMapper", tagMapper);
        ReflectionTestUtils.setField(dashboardService, "statisticsArticlePVDOMapper", statisticsArticlePVDOMapper);
        LoginUserContextHolder.setUserId(100L);
    }

    @AfterEach
    void tearDown() {
        LoginUserContextHolder.remove();
    }

    @Test
    void shouldAggregateDashboardStatistics() {
        when(articleMapper.selectCount(any())).thenReturn(5L);
        when(categoryMapper.selectCount(any())).thenReturn(2L);
        when(tagMapper.selectCount(any())).thenReturn(3L);
        when(articleMapper.selectAllViewCount(100L)).thenReturn(List.of(
                ArticleDO.builder().viewCount(12L).build(),
                ArticleDO.builder().viewCount(30L).build()
        ));

        Response<FindDashboardStatisticsInfoRspVO> response = dashboardService.findDashboardStatistics();

        assertTrue(response.isSuccess());
        assertEquals(5L, response.getData().getArticleTotalCount());
        assertEquals(2L, response.getData().getCategoryTotalCount());
        assertEquals(3L, response.getData().getTagTotalCount());
        assertEquals(42L, response.getData().getPvTotalCount());
    }

    @Test
    void shouldMapPublishArticleStatistics() {
        List<Map<String, Object>> rawData = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("publish_date", LocalDate.of(2026, 3, 18));
        row.put("article_count", 7);
        rawData.add(row);
        when(articleMapper.selectPublishArticleStatisticsLastYearRaw(any(), anyLong())).thenReturn(rawData);

        Response<Map<String, Long>> response = dashboardService.findDashboardPublishArticleStatistics();

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(7L, response.getData().get("2026-03-18"));
    }

    @Test
    void shouldFillMissingPvDatesWithZero() {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> rawData = new ArrayList<>();
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("pv_date", today.minusDays(6));
        row1.put("pv_count", 3);
        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("pv_date", today);
        row2.put("pv_count", 10);
        rawData.add(row1);
        rawData.add(row2);
        when(statisticsArticlePVDOMapper.selectPVStatisticsByDateRange(any(), any(), anyLong())).thenReturn(rawData);

        Response<FindDashboardPVStatisticsInfoRspVO> response = dashboardService.findDashboardPVStatistics();

        assertTrue(response.isSuccess());
        assertEquals(7, response.getData().getPvDates().size());
        assertEquals(7, response.getData().getPvCounts().size());
        assertEquals(3L, response.getData().getPvCounts().get(0));
        assertEquals(10L, response.getData().getPvCounts().get(6));
        assertEquals(today.minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                response.getData().getPvDates().get(0));
    }
}
