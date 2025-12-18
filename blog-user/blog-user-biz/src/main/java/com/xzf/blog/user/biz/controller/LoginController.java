package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.service.LoginService;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

//    @PostMapping("/login")
//    @ApiOperationLog(description = "用户登录/注册")
//    public Response<String> loginAndRegister(@Validated @RequestBody UserLoginRequest userLoginRequest) {
//        return loginService.loginAndRegister(userLoginRequest);
//    }
//
//    @PostMapping("/logout")
//    @ApiOperationLog(description = "账号登出")
//    public Response<?> logout() {
//        return loginService.logout();
//    }
//
//    @PostMapping("/password/update")
//    @ApiOperationLog(description = "修改密码")
//    public Response<?> updatePassword(@Validated @RequestBody UpdatePasswordReqVO updatePasswordReqVO) {
//        return loginService.updatePassword(updatePasswordReqVO);
//    }
//
//    @PostMapping("/verification/code/send")
//    @ApiOperationLog(description = "发送短信验证码")
//    public Response<?> send(@Validated @RequestBody SendVerificationCodeReqVO sendVerificationCodeReqVO) {
//        return verificationCodeService.send(sendVerificationCodeReqVO);
//    }

}
