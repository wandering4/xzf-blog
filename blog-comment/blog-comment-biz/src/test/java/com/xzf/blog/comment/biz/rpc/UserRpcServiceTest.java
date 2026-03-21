package com.xzf.blog.comment.biz.rpc;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.api.UserFeignApi;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
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
class UserRpcServiceTest {

    private UserRpcService userRpcService;

    @Mock
    private UserFeignApi userFeignApi;

    @BeforeEach
    void setUp() {
        userRpcService = new UserRpcService();
        ReflectionTestUtils.setField(userRpcService, "userFeignApi", userFeignApi);
    }

    @Test
    void getByIdsShouldReturnDataWhenFeignCallSucceeds() {
        when(userFeignApi.findByIds(any())).thenReturn(Response.success(List.of(FindUserByIdResponse.builder().id(1L).build())));

        assertThat(userRpcService.getByIds(List.of(1L))).hasSize(1);
    }

    @Test
    void getByIdsShouldReturnEmptyListWhenFeignCallFails() {
        when(userFeignApi.findByIds(any())).thenReturn(Response.fail("E", "fail"));

        assertThat(userRpcService.getByIds(List.of(1L))).isEmpty();
    }

    @Test
    void getByIdsShouldReturnEmptyListWithoutFeignCallWhenIdsAreEmpty() {
        assertThat(userRpcService.getByIds(List.of())).isEmpty();
        verifyNoInteractions(userFeignApi);
    }
}
