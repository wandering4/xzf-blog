package com.xzf.blog.comment.biz.controller;

import com.xzf.blog.comment.dto.request.DeleteCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.biz.service.CommentService;
import com.xzf.blog.comment.dto.response.FindCommentPageListRspVO;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
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
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/list")
    @ApiOperationLog(description = "查询评论分页数据")
    public PageResponse<FindCommentPageListRspVO> findCommentPageList(@RequestBody @Validated BasePageQuery req) {
        return commentService.findCommentPageList(req);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "评论删除")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return commentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/publish")
    @ApiOperationLog(description = "发布评论")
    public Response<Long> publishComment(@RequestBody @Validated PublishCommentReqVO publishCommentReqVO) {
        return commentService.publishComment(publishCommentReqVO);
    }

}
