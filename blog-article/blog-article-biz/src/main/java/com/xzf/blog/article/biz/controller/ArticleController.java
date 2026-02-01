package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.article.biz.service.ArticleService;
import com.xzf.blog.article.dto.request.IdsRequest;
import com.xzf.blog.article.dto.request.article.*;
import com.xzf.blog.article.dto.response.article.FindArticleDetailRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.aspect.PreAuthorize;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/list")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public PageResponse<FindIndexArticlePageListRspVO> findArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }

    @PostMapping("/getByIds")
    @ApiOperationLog(description = "获取文章数据")
    public Response<List<FindIndexArticlePageListRspVO>> getByIds(@RequestBody IdsRequest req) {
        return articleService.getByIds(req);
    }

    @PostMapping("/personal/list")
    @ApiOperationLog(description = "获取个人主页文章管理分页数据")
    public PageResponse<FindIndexArticlePageListRspVO> findPersonalArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findPersonalArticlePageList(findIndexArticlePageListReqVO);
    }


    @PostMapping("/detail")
    @ApiOperationLog(description = "获取文章详情")
    public Response<FindArticleDetailRspVO> findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }


    @PostMapping("/publish")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize
    public Response<?> publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "文章删除")
    public Response<?> deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "更新文章")
    public Response<?> updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/updateSummary")
    @ApiOperationLog(description = "更新摘要")
    public Response<?> updateArticleSummary(@RequestBody @Validated UpdateArticleSummaryRequest req) {
        return articleService.updateArticleSummary(req);
    }

    @PostMapping("/isTop/update")
    @ApiOperationLog(description = "文章置顶")
    @PreAuthorize(hasRoles = "root")
    public Response updateArticleIsTop(@RequestBody @Validated UpdateArticleIsTopReqVO updateArticleIsTopReqVO) {
        return articleService.updateArticleIsTop(updateArticleIsTopReqVO);
    }


}
