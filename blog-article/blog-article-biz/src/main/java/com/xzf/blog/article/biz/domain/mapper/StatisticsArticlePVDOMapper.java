package com.xzf.blog.article.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzf.blog.article.biz.domain.dataobject.StatisticsArticlePVDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsArticlePVDOMapper extends BaseMapper<StatisticsArticlePVDO> {

    /**
     * 查询最近一周的PV统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return PV统计数据列表
     */
    @Select("SELECT pv_date, pv_count FROM statistics_article_pv " +
            "WHERE pv_date >= #{startDate} "+
            "AND pv_date <= #{endDate} " +
            "AND author_id <= #{authorId} " +
            "ORDER BY pv_date")
    List<Map<String, Object>> selectPVStatisticsByDateRange(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate,
                                                           @Param("authorId") Long authorId);

    /**
     * 获取指定日期的PV统计记录
     * @param pvDate 统计日期
     * @return PV统计记录
     */
    @Select("SELECT * FROM statistics_article_pv WHERE pv_date = #{pvDate}")
    StatisticsArticlePVDO selectByPvDate(@Param("pvDate") LocalDate pvDate);
}