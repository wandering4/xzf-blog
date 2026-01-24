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
                                           LocalDate startDate, LocalDate endDate, Integer type, Long authorId) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)  // 小于等于 endDate
                .eq(ArticleDO::getStatus,ArticleStatusEnum.ENABLE.getCode()) // 文章类型
                .eq(Objects.nonNull(authorId),ArticleDO::getAuthorId,authorId)
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 分页查询（支持articleId列表过滤）
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @param articleIds
     * @return
     */
    default Page<ArticleDO> selectPageListWithArticleIds(Long current, Long size, String title,
                                                        LocalDate startDate, LocalDate endDate,
                                                        List<Long> articleIds, Long authorId) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)  // 小于等于 endDate
                .eq(ArticleDO::getStatus,ArticleStatusEnum.ENABLE.getCode()) // 文章状态
                .in(Objects.nonNull(articleIds) && !articleIds.isEmpty(), ArticleDO::getId, articleIds) // articleId 过滤
                .eq(Objects.nonNull(authorId),ArticleDO::getAuthorId,authorId)
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    default List<ArticleDO> selectAllViewCount(Long userId) {
        // 设置仅查询 read_num 字段
        return selectList(Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getAuthorId, userId)
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
            "AND author_id = #{userId} " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY publish_date")
    List<Map<String, Object>> selectPublishArticleStatisticsLastYearRaw(@Param("status") Integer status, @Param("userId") Long userId);

    /**
     * 获取所有文章的总浏览量
     * @return 总浏览量
     */
    @Select("SELECT SUM(view_count) FROM article WHERE status = #{status}")
    Long getTotalViewCount(@Param("status") Integer status);

    /**
     * 阅读量+1
     * @param articleId
     * @return
     */
    default int increaseReadNum(Long articleId) {
        return update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(ArticleDO::getId, articleId));
    }

}