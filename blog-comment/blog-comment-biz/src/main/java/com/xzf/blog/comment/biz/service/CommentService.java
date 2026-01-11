package com.xzf.blog.comment.biz.service;

import com.xzf.blog.comment.dto.request.CommentIdReqVO;
import com.xzf.blog.comment.dto.request.CountCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.dto.response.CommentCountVO;
import com.xzf.blog.comment.dto.response.FindCommentPageListVO;
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
     * @param commentIdReqVO
     * @return
     */
    Response deleteComment(CommentIdReqVO commentIdReqVO);

    /**
     * 查询评论分页数据
     * @param req
     * @return
     */
    PageResponse<FindCommentPageListVO> findCommentPageList(BasePageQuery req);

    /**
     * 获取评论数
     * @param req
     * @return
     */
    Response<CommentCountVO> count(CountCommentReqVO req);
}
