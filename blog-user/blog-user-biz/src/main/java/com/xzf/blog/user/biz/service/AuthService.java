package com.xzf.blog.user.biz.service;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.SendVerificationCodeReqVO;
import com.xzf.blog.user.biz.model.vo.request.UpdatePasswordReqVO;
import com.xzf.blog.user.biz.model.vo.request.UserLoginRequest;

public interface AuthService {
    /**
     * 登录与注册
     * @param userLoginRequest
     * @return
     */
    Response<String> loginAndRegister(UserLoginRequest userLoginRequest);


    /**
     * 退出登录
     * @return
     */
    Response<?> logout();


    /**
     * 修改密码
     * @param updatePasswordReqVO
     * @return
     */
    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);

    /**
     * 发送短信验证码
     *
     * @param sendVerificationCodeReqVO
     * @return
     */
    Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);


}
