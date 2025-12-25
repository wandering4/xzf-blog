package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.request.article.FindArticleDetailReqVO;
import com.xzf.blog.article.dto.request.article.FindIndexArticlePageListReqVO;
import com.xzf.blog.article.dto.request.article.PublishArticleReqVO;
import com.xzf.blog.framework.commons.response.Response;

public interface ArticleService {

    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response<?> publishArticle(PublishArticleReqVO publishArticleReqVO);
}
