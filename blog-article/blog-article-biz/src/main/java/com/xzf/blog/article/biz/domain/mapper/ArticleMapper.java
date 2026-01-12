package com.xzf.blog.article.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.enums.ArticleStatusEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 分页查询
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title,
                                           LocalDate startDate, LocalDate endDate, Integer type) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)  // 小于等于 endDate
                .eq(ArticleDO::getStatus,ArticleStatusEnum.ENABLE.getCode()) // 文章类型
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    default List<ArticleDO> selectAllViewCount(){
        // 设置仅查询 read_num 字段
        return selectList(Wrappers.<ArticleDO>lambdaQuery()
                .select(ArticleDO::getViewCount));
    }

    /**
     * 查询过去一年内每天的文章发布数量统计
     * @return 日期 -> 发布数量的映射
     */
    @Select("SELECT DATE(create_time) as publish_date, COUNT(*) as article_count " +
            "FROM article " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
            "AND status = #{status} " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY publish_date")
    List<Map<String, Object>> selectPublishArticleStatisticsLastYearRaw(@Param("status") Integer status);

    /**
     * 获取所有文章的总浏览量
     * @return 总浏览量
     */
    @Select("SELECT SUM(view_count) FROM article WHERE status = #{status}")
    Long getTotalViewCount(@Param("status") Integer status);

}