package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.article.dto.request.IdsRequest;
import com.xzf.blog.article.dto.request.article.*;
import com.xzf.blog.article.dto.response.article.FindArticleDetailRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;

import java.util.List;

public interface ArticleService {

    /**
     * 获取首页文章分页数据
     *
     * @param findIndexArticlePageListReqVO
     * @return
     */
    PageResponse<FindIndexArticlePageListRspVO> findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    public Response<List<FindIndexArticlePageListRspVO>> getByIds(IdsRequest req);

    /**
     * 获取文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    Response<FindArticleDetailRspVO> findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    Response<?> publishArticle(PublishArticleReqVO publishArticleReqVO);

    /**
     * 删除文章
     *
     * @param deleteArticleReqVO
     * @return
     */
    Response<?> deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    /**
     * 更新文章
     *
     * @param updateArticleReqVO
     * @return
     */
    Response updateArticle(UpdateArticleReqVO updateArticleReqVO);

    /**
     * 更新文章摘要
     *
     * @param req
     * @return
     */
    Response<?> updateArticleSummary(UpdateArticleSummaryRequest req);

    /**
     * 更新文章是否置顶
     *
     * @param updateArticleIsTopReqVO
     * @return
     */
    Response<?> updateArticleIsTop(UpdateArticleIsTopReqVO updateArticleIsTopReqVO);


    PageResponse<FindIndexArticlePageListRspVO> findPersonalArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

}
