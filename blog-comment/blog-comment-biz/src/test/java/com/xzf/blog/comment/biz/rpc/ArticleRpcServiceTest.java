package com.xzf.blog.comment.biz.rpc;

import com.xzf.blog.article.api.ArticleFeignApi;
import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.framework.commons.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleRpcServiceTest {

    private ArticleRpcService articleRpcService;

    @Mock
    private ArticleFeignApi articleFeignApi;

    @BeforeEach
    void setUp() {
        articleRpcService = new ArticleRpcService();
        ReflectionTestUtils.setField(articleRpcService, "articleFeignApi", articleFeignApi);
    }

    @Test
    void getByIdsShouldReturnDataWhenFeignCallSucceeds() {
        when(articleFeignApi.getByIds(any()))
                .thenReturn(Response.success(List.of(FindIndexArticlePageListRspVO.builder().id(2L).title("post").build())));

        assertThat(articleRpcService.getByIds(List.of(2L))).hasSize(1);
    }

    @Test
    void getByIdsShouldReturnEmptyListWhenFeignCallFails() {
        when(articleFeignApi.getByIds(any())).thenReturn(Response.fail("E", "fail"));

        assertThat(articleRpcService.getByIds(List.of(2L))).isEmpty();
    }

    @Test
    void getByIdsShouldReturnEmptyListWithoutFeignCallWhenIdsAreEmpty() {
        assertThat(articleRpcService.getByIds(List.of())).isEmpty();
        verifyNoInteractions(articleFeignApi);
    }
}
