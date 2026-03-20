package com.xzf.blog.user.biz.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.xzf.blog.framework.commons.constant.RedisKeyConstants;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.manager.RoleManager;
import com.xzf.blog.user.biz.model.vo.request.SendVerificationCodeReqVO;
import com.xzf.blog.user.biz.model.vo.request.UserLoginRequest;
import com.xzf.blog.user.biz.service.UserService;
import com.xzf.blog.user.dto.req.FindUserByPhoneRequest;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
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

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @Mock
    private UserService userService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Mock
    private com.xzf.blog.user.biz.sms.AliyunSmsHelper aliyunSmsHelper;
    @Mock
    private RoleManager roleManager;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl();
        ReflectionTestUtils.setField(authService, "userService", userService);
        ReflectionTestUtils.setField(authService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "threadPoolTaskExecutor", threadPoolTaskExecutor);
        ReflectionTestUtils.setField(authService, "aliyunSmsHelper", aliyunSmsHelper);
        ReflectionTestUtils.setField(authService, "roleManager", roleManager);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void loginAndRegisterShouldLoginWithVerificationCode() {
        UserLoginRequest request = UserLoginRequest.builder()
                .phone("13800138000")
                .code("123456")
                .type(1)
                .build();
        when(valueOperations.get(RedisKeyConstants.buildVerificationCodeKey("13800138000"))).thenReturn("123456");
        when(userService.register(any())).thenReturn(Response.success(12L));
        when(roleManager.selectByUserId(12L)).thenReturn(RoleDO.builder().roleKey("admin").build());

        try (MockedStatic<StpUtil> stpUtil = org.mockito.Mockito.mockStatic(StpUtil.class)) {
            SaTokenInfo tokenInfo = new SaTokenInfo();
            tokenInfo.tokenValue = "token-123";
            stpUtil.when(() -> StpUtil.getTokenInfo()).thenReturn(tokenInfo);

            Response<String> response = authService.loginAndRegister(request);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEqualTo("token-123");
            stpUtil.verify(() -> StpUtil.login(12L));
            verify(valueOperations).set(
                    eq(RedisKeyConstants.buildUserRoleKey(12L)),
                    eq("admin"),
                    eq(72L),
                    eq(TimeUnit.HOURS)
            );
        }
    }

    @Test
    void loginAndRegisterShouldRejectWrongPassword() {
        UserLoginRequest request = UserLoginRequest.builder()
                .phone("13800138000")
                .password("bad-password")
                .type(2)
                .build();
        when(userService.findByPhone(any(FindUserByPhoneRequest.class)))
                .thenReturn(FindUserByPhoneRspDTO.builder().id(5L).password("encoded").build());
        when(passwordEncoder.matches("bad-password", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.loginAndRegister(request)).isInstanceOf(BizException.class);
    }

    @Test
    void logoutShouldRemoveTokenAndRedisRoleCache() {
        try (MockedStatic<com.xzf.framework.biz.context.holder.LoginUserContextHolder> contextHolder =
                     org.mockito.Mockito.mockStatic(com.xzf.framework.biz.context.holder.LoginUserContextHolder.class);
             MockedStatic<StpUtil> stpUtil = org.mockito.Mockito.mockStatic(StpUtil.class)) {
            contextHolder.when(com.xzf.framework.biz.context.holder.LoginUserContextHolder::getUserId).thenReturn(9L);

            Response<?> response = authService.logout();

            assertThat(response.isSuccess()).isTrue();
            stpUtil.verify(() -> StpUtil.logout(9L));
            verify(redisTemplate).delete(RedisKeyConstants.buildUserRoleKey(9L));
        }
    }

    @Test
    void sendShouldRejectWhenVerificationCodeAlreadyExists() {
        SendVerificationCodeReqVO request = SendVerificationCodeReqVO.builder()
                .phone("13800138000")
                .pictureId("pic-1")
                .pictureResult("a1bc")
                .build();
        when(valueOperations.get(RedisKeyConstants.buildVerificationPictureKey("pic-1"))).thenReturn("A1BC");
        when(redisTemplate.hasKey(RedisKeyConstants.buildVerificationCodeKey("13800138000"))).thenReturn(true);

        assertThatThrownBy(() -> authService.send(request)).isInstanceOf(BizException.class);
    }
}
