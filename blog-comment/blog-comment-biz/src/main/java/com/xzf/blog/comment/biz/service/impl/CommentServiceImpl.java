package com.xzf.blog.comment.biz.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.comment.biz.convert.CommentConvert;
import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;
import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import com.xzf.blog.comment.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.comment.biz.service.CommentService;
import com.xzf.blog.comment.dto.request.DeleteCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.dto.response.FindCommentPageListRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommentDOMapper commentDOMapper;

    @Override
    public Response<Long> publishComment(PublishCommentReqVO publishCommentReqVO) {
        Long userId= LoginUserContextHolder.getUserId();
        CommentDO commentDO = CommentDO.builder()
                .userId(userId)
                .content(publishCommentReqVO.getContent())
                .articleId(publishCommentReqVO.getArticleId())
                .build();
        commentDOMapper.insert(commentDO);
        // 评论发布事件
//        rocketMQTemplate.asyncSend();
        return Response.success(commentDO.getId());
    }

    @Override
    public Response deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long userId= LoginUserContextHolder.getUserId();
        CommentDO commentDO = commentDOMapper.selectById(deleteCommentReqVO.getId());
        if(ObjectUtil.isNull(commentDO)){
            log.warn("该评论不存在, commentId: {}", deleteCommentReqVO.getId());
            throw new BizException(BizResponseCodeEnum.COMMENT_NOT_FOUND);
        }
        // TODO:不是本人或者管理员
        if(!commentDO.getUserId().equals(userId)){
            throw new BizException(BizResponseCodeEnum.NOT_HAVE_PERMISSION);
        }
        commentDOMapper.deleteById(deleteCommentReqVO.getId());
        // 评论删除事件
//        rocketMQTemplate.asyncSend();
        return Response.success();
    }

    @Override
    public PageResponse<FindCommentPageListRspVO> findCommentPageList(BasePageQuery req) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = req.getCurrent();
        Long size = req.getSize();

        // 执行分页查询
        Page<CommentDO> commentDOPage = commentDOMapper.selectPageList(current, size);

        List<CommentDO> commentDOS = commentDOPage.getRecords();

        // DO 转 VO
        List<FindCommentPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(commentDOS)) {
            vos = commentDOS.stream()
                    .map(CommentConvert.INSTANCE::convertDO2VO)
                    .collect(Collectors.toList());
        }
        return PageResponse.success(commentDOPage, vos);
    }
}
