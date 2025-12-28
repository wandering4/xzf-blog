package com.xzf.blog.article.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xzf.blog.article.biz.domain.dataobject.ArticleTagDO;

import java.util.List;

public interface ArticleTagMapper extends BaseMapper<ArticleTagDO> {

    /**
     * 根据文章 ID 删除关联记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleTagDO>lambdaQuery()
                .eq(ArticleTagDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 来查询
     * @param articleId
     * @return
     */
    default List<ArticleTagDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<ArticleTagDO>lambdaQuery()
                .eq(ArticleTagDO::getArticleId, articleId));
    }

    /**
     * 根据标签 ID 查询
     * @param tagId
     * @return
     */
    default ArticleTagDO selectOneByTagId(Long tagId) {
        return selectOne(Wrappers.<ArticleTagDO>lambdaQuery()
                .eq(ArticleTagDO::getTagId, tagId)
                .last("LIMIT 1"));
    }

    /**
     * 根据文章 ID 集合批量查询
     * @param articleIds
     * @return
     */
    default List<ArticleTagDO> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleTagDO>lambdaQuery()
                .in(ArticleTagDO::getArticleId, articleIds));
    }

    /**
     * 查询该标签 ID 下所有关联记录
     * @param tagId
     * @return
     */
    default List<ArticleTagDO> selectByTagId(Long tagId) {
        return selectList(Wrappers.<ArticleTagDO>lambdaQuery()
                .eq(ArticleTagDO::getTagId, tagId));
    }

}