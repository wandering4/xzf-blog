package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.SendVerificationCodeReqVO;
import com.xzf.blog.user.biz.model.vo.request.UserLoginRequest;
import com.xzf.blog.user.biz.service.AuthService;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @GetMapping("/verification/picture/get")
    @ApiOperationLog(description = "获取验证图片")
    public void getVerificationPicture(@NotBlank(message = "图片id不能为空") String pictureId, HttpServletResponse response) {
        authService.getVerificationPicture(pictureId, response);
    }

    @PostMapping("/verification/code/send")
    @ApiOperationLog(description = "发送短信验证码")
    public Response<?> send(@Validated @RequestBody SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        return authService.send(sendVerificationCodeReqVO);
    }

    @PostMapping("/login")
    @ApiOperationLog(description = "用户登录/注册")
    public Response<String> loginAndRegister(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        return authService.loginAndRegister(userLoginRequest);
    }

    @PostMapping("/logout")
    @ApiOperationLog(description = "账号登出")
    public Response<?> logout() {
        return authService.logout();
    }


}
