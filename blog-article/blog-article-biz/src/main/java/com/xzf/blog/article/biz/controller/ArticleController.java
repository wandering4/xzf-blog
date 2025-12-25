package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.biz.service.ArticleService;
import com.xzf.blog.article.dto.request.article.PublishArticleReqVO;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {


    @Autowired
    private ArticleService articleService;

    @PostMapping("/list")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public Response<?> findArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }


    @PostMapping("/detail")
    @ApiOperationLog(description = "获取文章详情")
    public Response<?> findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }


    @PostMapping("/publish")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<?> publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "文章删除")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<?> deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "查询文章分页数据")
    public Response<?> findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @ApiOperationLog(description = "查询文章详情")
    public Response<?> findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "更新文章")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<?> updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/isTop/update")
    @ApiOperationLog(description = "更新文章置顶状态")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<?> updateArticleIsTop(@RequestBody @Validated UpdateArticleIsTopReqVO updateArticleIsTopReqVO) {
        return articleService.updateArticleIsTop(updateArticleIsTopReqVO);
    }


}
