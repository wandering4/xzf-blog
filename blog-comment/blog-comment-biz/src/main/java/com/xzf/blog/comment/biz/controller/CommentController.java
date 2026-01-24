package com.xzf.blog.comment.biz.controller;

import com.xzf.blog.comment.dto.request.CommentIdReqVO;
import com.xzf.blog.comment.dto.request.CommentPageRequest;
import com.xzf.blog.comment.dto.request.CountCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.biz.service.CommentService;
import com.xzf.blog.comment.dto.response.CommentCountVO;
import com.xzf.blog.comment.dto.response.FindCommentPageListVO;
import com.xzf.blog.framework.commons.request.BasePageQuery;
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

@Slf4j
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/list")
    @ApiOperationLog(description = "查询评论分页数据")
    public PageResponse<FindCommentPageListVO> findCommentPageList(@RequestBody @Validated CommentPageRequest req) {
        return commentService.findCommentPageList(req);
    }

    @PostMapping("/count")
    @ApiOperationLog(description = "获取评论数")
    public Response<CommentCountVO> countComment(@RequestBody @Validated CountCommentReqVO req) {
        return commentService.count(req);
    }

    @PostMapping("/delete")
    @PreAuthorize
    @ApiOperationLog(description = "评论删除")
    public Response deleteComment(@RequestBody @Validated CommentIdReqVO commentIdReqVO) {
        return commentService.deleteComment(commentIdReqVO);
    }

    @PostMapping("/publish")
    @PreAuthorize
    @ApiOperationLog(description = "发布评论")
    public Response<Long> publishComment(@RequestBody @Validated PublishCommentReqVO publishCommentReqVO) {
        return commentService.publishComment(publishCommentReqVO);
    }



}
