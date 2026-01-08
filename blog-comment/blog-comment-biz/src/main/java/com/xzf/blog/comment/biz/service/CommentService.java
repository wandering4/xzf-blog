package com.xzf.blog.comment.biz.service;

import com.xzf.blog.comment.dto.request.DeleteCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.dto.response.FindCommentPageListRspVO;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;

public interface CommentService {

    /**
     * 发布评论
     * @param publishCommentReqVO
     * @return
     */
    Response<Long> publishComment(PublishCommentReqVO publishCommentReqVO);

    /**
     * 删除评论
     * @param deleteCommentReqVO
     * @return
     */
    Response deleteComment(DeleteCommentReqVO deleteCommentReqVO);

    /**
     * 查询评论分页数据
     * @param req
     * @return
     */
    PageResponse<FindCommentPageListRspVO> findCommentPageList(BasePageQuery req);
}
