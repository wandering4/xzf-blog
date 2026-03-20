package com.xzf.blog.user.biz.service.impl;

import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.constant.RedisKeyConstants;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import com.xzf.blog.user.biz.domain.dataobject.UserRoleDO;
import com.xzf.blog.user.biz.domain.mapper.RoleDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserRoleDOMapper;
import com.xzf.blog.user.biz.model.vo.request.UpdateUserInfoRequest;
import com.xzf.blog.user.biz.rpc.OssRpcService;
import com.xzf.blog.user.dto.req.RegisterUserRequest;
import com.xzf.blog.user.dto.req.UserIdRequest;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserDOMapper userDOMapper;
    @Mock
    private OssRpcService ossRpcService;
    @Mock
    private UserRoleDOMapper userRoleDOMapper;
    @Mock
    private RoleDOMapper roleDOMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RocketMQTemplate rocketMQTemplate;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "userDOMapper", userDOMapper);
        ReflectionTestUtils.setField(userService, "ossRpcService", ossRpcService);
        ReflectionTestUtils.setField(userService, "userRoleDOMapper", userRoleDOMapper);
        ReflectionTestUtils.setField(userService, "roleDOMapper", roleDOMapper);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "rocketMQTemplate", rocketMQTemplate);
        ReflectionTestUtils.setField(userService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(userService, "threadPoolTaskExecutor", threadPoolTaskExecutor);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }).when(threadPoolTaskExecutor).submit(any(Runnable.class));
        doNothing().when(rocketMQTemplate).asyncSend(any(), any(), any(), anyLong(), anyInt());
    }

    @Test
    void registerShouldReturnExistingUserId() {
        when(userDOMapper.selectByPhone("13800138000")).thenReturn(UserDO.builder().id(3L).phone("13800138000").build());

        Response<Long> response = userService.register(RegisterUserRequest.builder().phone("13800138000").build());

        assertThat(response.getData()).isEqualTo(3L);
    }

    @Test
    void registerShouldCreateUserAndDefaultRole() {
        when(userDOMapper.selectByPhone("13800138000")).thenReturn(null);
        doAnswer(invocation -> {
            UserDO user = invocation.getArgument(0);
            user.setId(10L);
            return 1;
        }).when(userDOMapper).insert(any(UserDO.class));

        Response<Long> response = userService.register(RegisterUserRequest.builder().phone("13800138000").build());

        assertThat(response.getData()).isEqualTo(10L);
        verify(userRoleDOMapper).insert(any(UserRoleDO.class));
    }

    @Test
    void updateUserInfoShouldDeleteCacheUpdateUserAndSendDelayMessage() {
        UserDO current = UserDO.builder()
                .id(8L)
                .username("old-name")
                .avatarUrl("old-avatar")
                .introduction("old-intro")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        when(userDOMapper.selectById(8L)).thenReturn(current);

        try (MockedStatic<com.xzf.framework.biz.context.holder.LoginUserContextHolder> contextHolder =
                     org.mockito.Mockito.mockStatic(com.xzf.framework.biz.context.holder.LoginUserContextHolder.class)) {
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserId).thenReturn(8L);

            Response<?> response = userService.updateUserInfo(UpdateUserInfoRequest.builder()
                    .nickname("new-name")
                    .avatarUrl("new-avatar")
                    .introduction("new-intro")
                    .build());

            assertThat(response.isSuccess()).isTrue();
            verify(redisTemplate).delete(List.of(
                    RedisKeyConstants.buildUserInfoKey(8L),
                    RedisKeyConstants.buildUserProfileKey(8L)
            ));
            verify(userDOMapper).updateById(current);
            verify(rocketMQTemplate).asyncSend(any(), any(), any(), anyLong(), anyInt());
        }
    }

    @Test
    void findByIdShouldReturnCachedUser() {
        String key = RedisKeyConstants.buildUserInfoKey(6L);
        when(valueOperations.get(key)).thenReturn("{\"id\":6,\"userName\":\"cached\",\"avatarUrl\":\"a\",\"introduction\":\"i\"}");

        Response<FindUserByIdResponse> response = userService.findById(UserIdRequest.builder().id(6L).build());

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getUserName()).isEqualTo("cached");
    }

    @Test
    void findByIdShouldCacheNullMarkerAndThrowWhenUserMissing() {
        String key = RedisKeyConstants.buildUserInfoKey(99L);
        when(valueOperations.get(key)).thenReturn(null);
        when(userDOMapper.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> userService.findById(UserIdRequest.builder().id(99L).build()))
                .isInstanceOf(BizException.class);

        verify(valueOperations).set(eq(key), eq("null"), anyLong(), eq(TimeUnit.SECONDS));
    }
}
