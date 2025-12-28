package com.xzf.blog.article.biz.convert;

import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.biz.model.vo.article.FindIndexArticlePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023/10/8 14:57
 * @description: 文章转换
 **/
@Mapper
public interface ArticleConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);

    /**
     * ArticleDO -> FindIndexArticlePageListRspVO
     *
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    FindIndexArticlePageListRspVO convertDO2VO(ArticleDO bean);

    /**
     * ArticleDO -> FindArchiveArticleRspVO
     * @param bean
     * @return
     */
//    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
//    @Mapping(target = "createMonth", expression = "java(java.time.YearMonth.from(bean.getCreateTime()))")
//    FindArchiveArticleRspVO convertDO2ArchiveArticleVO(ArticleDO bean);

    /**
     * ArticleDO -> FindCategoryArticlePageListRspVO
     * @param bean
     * @return
     */
//    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
//    FindCategoryArticlePageListRspVO convertDO2CategoryArticleVO(ArticleDO bean);

    /**
     * ArticleDO -> FindTagArticlePageListRspVO
     * @param bean
     * @return
     */
//    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
//    FindTagArticlePageListRspVO convertDO2TagArticleVO(ArticleDO bean);
}
