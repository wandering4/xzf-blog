package com.xzf.blog.user.biz.service;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.UpdatePasswordRequest;
import com.xzf.blog.user.biz.model.vo.request.UpdateUserInfoRequest;
import com.xzf.blog.user.dto.req.*;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
import com.xzf.blog.user.dto.resp.LoginUserInfoResponse;

import java.util.List;

public interface UserService {

    /**
     * 更新用户信息
     *
     * @param updateUserInfoRequest
     * @return
     */
    Response<?> updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest);

    /**
     * 用户注册
     *
     * @param registerUserRequest
     * @return
     */
    Response<Long> register(RegisterUserRequest registerUserRequest);

    /**
     * 根据手机号查询用户信息
     *
     * @param findUserByPhoneRequest
     * @return
     */
    FindUserByPhoneRspDTO findByPhone(FindUserByPhoneRequest findUserByPhoneRequest);

    /**
     * 更新密码
     *
     * @param updatePasswordRequest
     * @return
     */
    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param userIdRequest
     * @return
     */
    Response<FindUserByIdResponse> findById(UserIdRequest userIdRequest);

    /**
     * 批量根据用户 ID 查询用户信息
     *
     * @param findUsersByIdsReqDTO
     * @return
     */
    Response<List<FindUserByIdResponse>> findByIds(FindUsersByIdsReqDTO findUsersByIdsReqDTO);

    Response<?> deleteUser(UserIdRequest req);

    Response<LoginUserInfoResponse> getLoginUserInfo();
}
