package com.xzf.blog.comment.biz.service.impl;

import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;
import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import com.xzf.blog.comment.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.comment.biz.rpc.ArticleRpcService;
import com.xzf.blog.comment.biz.rpc.UserRpcService;
import com.xzf.blog.comment.dto.request.CommentIdReqVO;
import com.xzf.blog.comment.dto.request.CountCommentReqVO;
import com.xzf.blog.comment.dto.request.PublishCommentReqVO;
import com.xzf.blog.comment.dto.response.CommentCountVO;
import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {

    private CommentServiceImpl commentService;

    @Mock
    private CommentDOMapper commentDOMapper;
    @Mock
    private UserRpcService userRpcService;
    @Mock
    private ArticleRpcService articleRpcService;
    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl();
        ReflectionTestUtils.setField(commentService, "commentDOMapper", commentDOMapper);
        ReflectionTestUtils.setField(commentService, "userRpcService", userRpcService);
        ReflectionTestUtils.setField(commentService, "articleRpcService", articleRpcService);
        ReflectionTestUtils.setField(commentService, "threadPoolTaskExecutor", threadPoolTaskExecutor);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void publishCommentShouldThrowWhenArticleDoesNotExist() {
        when(articleRpcService.getByIds(List.of(10L))).thenReturn(Collections.emptyList());

        try (MockedStatic<com.xzf.framework.biz.context.holder.LoginUserContextHolder> contextHolder =
                     org.mockito.Mockito.mockStatic(com.xzf.framework.biz.context.holder.LoginUserContextHolder.class)) {
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserId).thenReturn(3L);

            assertThatThrownBy(() -> commentService.publishComment(PublishCommentReqVO.builder()
                    .articleId(10L)
                    .content("hello")
                    .build()))
                    .isInstanceOf(BizException.class);
        }
    }

    @Test
    void publishCommentShouldPersistAndReturnCommentId() {
        when(articleRpcService.getByIds(List.of(10L)))
                .thenReturn(List.of(FindIndexArticlePageListRspVO.builder().id(10L).title("article").build()));
        org.mockito.Mockito.doAnswer(invocation -> {
            CommentDO comment = invocation.getArgument(0);
            comment.setId(88L);
            return 1;
        }).when(commentDOMapper).insert(any(CommentDO.class));

        try (MockedStatic<com.xzf.framework.biz.context.holder.LoginUserContextHolder> contextHolder =
                     org.mockito.Mockito.mockStatic(com.xzf.framework.biz.context.holder.LoginUserContextHolder.class)) {
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserId).thenReturn(3L);

            Response<Long> response = commentService.publishComment(PublishCommentReqVO.builder()
                    .articleId(10L)
                    .content("hello")
                    .build());

            assertThat(response.getData()).isEqualTo(88L);
        }
    }

    @Test
    void deleteCommentShouldRejectUserWithoutPermission() {
        when(commentDOMapper.selectById(7L)).thenReturn(CommentDO.builder().id(7L).userId(99L).build());

        try (MockedStatic<com.xzf.framework.biz.context.holder.LoginUserContextHolder> contextHolder =
                     org.mockito.Mockito.mockStatic(com.xzf.framework.biz.context.holder.LoginUserContextHolder.class)) {
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserId).thenReturn(1L);
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserRole)
                    .thenReturn(RoleEnums.COMMON_USER.getName());

            assertThatThrownBy(() -> commentService.deleteComment(CommentIdReqVO.builder().id(7L).build()))
                    .isInstanceOf(BizException.class);
        }
    }

    @Test
    void countShouldFillMissingCountsWithZero() {
        when(commentDOMapper.count(List.of(1L, 2L, 3L))).thenReturn(Map.of(1L, 5L, 3L, 2L));

        Response<CommentCountVO> response = commentService.count(CountCommentReqVO.builder()
                .articleIdList(List.of(1L, 2L, 3L))
                .build());

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCommentCountItems()).hasSize(3);
        assertThat(response.getData().getCommentCountItems().get(1).getCount()).isZero();
        verify(commentDOMapper).count(List.of(1L, 2L, 3L));
    }
}
