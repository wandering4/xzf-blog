package com.xzf.blog.article.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xzf.blog.article.biz.domain.dataobject.ArticleCategoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleCategoryMapper extends BaseMapper<ArticleCategoryDO> {

    /**
     * 根据文章 ID 删除关联记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleCategoryDO>lambdaQuery()
                .eq(ArticleCategoryDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 查询
     * @param articleId
     * @return
     */
    default ArticleCategoryDO selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleCategoryDO>lambdaQuery()
                .eq(ArticleCategoryDO::getArticleId, articleId));
    }

    /**
     * 根据分类 ID 查询
     * @param categoryId
     * @return
     */
    default ArticleCategoryDO selectOneByCategoryId(Long categoryId) {
        return selectOne(Wrappers.<ArticleCategoryDO>lambdaQuery()
                .eq(ArticleCategoryDO::getCategoryId, categoryId)
                .last("LIMIT 1"));
    }

    /**
     * 根据文章 ID 集合批量查询
     * @param articleIds
     * @return
     */
    default List<ArticleCategoryDO> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleCategoryDO>lambdaQuery()
                .in(ArticleCategoryDO::getArticleId, articleIds));
    }

    /**
     * 根据分类 ID 查询所有的关联记录
     * @param categoryId
     * @return
     */
    default List<ArticleCategoryDO> selectListByCategoryId(Long categoryId) {
        return selectList(Wrappers.<ArticleCategoryDO>lambdaQuery()
                .eq(ArticleCategoryDO::getCategoryId, categoryId));
    }

    /**
     * 统计分类下的文章数量
     * @param categoryId
     * @return
     */
    int countArticlesByCategoryId(@Param("categoryId") Long categoryId);

}