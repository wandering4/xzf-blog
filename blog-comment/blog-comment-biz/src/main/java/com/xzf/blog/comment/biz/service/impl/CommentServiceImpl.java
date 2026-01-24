package com.xzf.blog.comment.biz.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.comment.biz.convert.CommentConvert;
import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;
import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import com.xzf.blog.comment.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.comment.biz.service.CommentService;
import com.xzf.blog.comment.dto.request.CommentIdReqVO;
import com.xzf.blog.comment.dto.request.CommentPageRequest;
import com.xzf.blog.comment.dto.request.CountCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.dto.response.CommentCountItem;
import com.xzf.blog.comment.dto.response.CommentCountVO;
import com.xzf.blog.comment.dto.response.FindCommentPageListVO;
import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

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
        return Response.success(commentDO.getId());
    }

    @Override
    public Response deleteComment(CommentIdReqVO commentIdReqVO) {
        Long userId= LoginUserContextHolder.getUserId();
        CommentDO commentDO = commentDOMapper.selectById(commentIdReqVO.getId());
        if(ObjectUtil.isNull(commentDO)){
            log.warn("该评论不存在, commentId: {}", commentIdReqVO.getId());
            throw new BizException(BizResponseCodeEnum.COMMENT_NOT_FOUND);
        }
        // 不是本人或者管理员
        String role = LoginUserContextHolder.getUserRole();
        if (!RoleEnums.ROOT.getName().equals(role) && !commentDO.getUserId().equals(userId)) {
            throw new BizException(BizResponseCodeEnum.NOT_HAVE_PERMISSION);
        }
        commentDOMapper.deleteById(commentIdReqVO.getId());
        return Response.success();
    }

    @Override
    public PageResponse<FindCommentPageListVO> findCommentPageList(CommentPageRequest req) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = req.getCurrent();
        Long size = req.getSize();
        Long articleId = req.getArticleId();
        Long userId = req.getUserId();

        // 执行分页查询
        Page<CommentDO> commentDOPage = commentDOMapper.selectPageList(current, size, articleId, userId, req.getStartDate(), req.getEndDate());

        List<CommentDO> commentDOS = commentDOPage.getRecords();

        List<Long> articleIds = commentDOS.stream().map(CommentDO::getArticleId).toList();
        List<Long> userIds = commentDOS.stream().map(CommentDO::getUserId).toList();

        // TODO:rpc查询信息

        // DO 转 VO
        List<FindCommentPageListVO> vos = null;
        if (!CollectionUtils.isEmpty(commentDOS)) {
            vos = commentDOS.stream()
                    .map(CommentConvert.INSTANCE::convertDO2VO)
                    .collect(Collectors.toList());
        }
        return PageResponse.success(commentDOPage, vos);
    }

    @Override
    public Response<CommentCountVO> count(CountCommentReqVO req) {
        List<Long> articleIdList = req.getArticleIdList();
        // select count(*) from comment where article_id in (:articleIds) group by article_id
        Map<Long,Long> articleCountMap=commentDOMapper.count(articleIdList);
        List<CommentCountItem> countItems = articleIdList.stream().map(articleId ->
                CommentCountItem.builder()
                        .articleId(articleId)
                        .count(articleCountMap.getOrDefault(articleId, 0L))
                        .build()
        ).toList();
        return Response.success(new CommentCountVO(countItems));
    }

}
