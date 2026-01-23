package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.UpdatePasswordRequest;
import com.xzf.blog.user.biz.model.vo.request.UpdateUserInfoRequest;
import com.xzf.blog.user.biz.service.UserService;
import com.xzf.blog.user.dto.req.*;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
import com.xzf.blog.user.dto.resp.LoginUserInfoResponse;
import com.xzf.framework.biz.context.aspect.PreAuthorize;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/password/update")
    @ApiOperationLog(description = "修改密码")
    public Response<?> updatePassword(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return userService.updatePassword(updatePasswordRequest);
    }

    /**
     * 用户信息修改
     * @param updateUserInfoRequest
     * @return
     */
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationLog(description = "用户信息修改")
    @PreAuthorize
    public Response<?> updateUserInfo(@Validated UpdateUserInfoRequest updateUserInfoRequest) {
        return userService.updateUserInfo(updateUserInfoRequest);
    }

    /**
     * 删除用户
     * @param req
     * @return
     */
    @PostMapping(value = "/delete")
    @ApiOperationLog(description = "删除用户")
    public Response<?> updateUserInfo(@Validated UserIdRequest req) {
        return userService.deleteUser(req);
    }

    @PostMapping("/findByPhone")
    @ApiOperationLog(description = "手机号查询用户信息")
    public Response<FindUserByPhoneRspDTO> findByPhone(@Validated @RequestBody FindUserByPhoneRequest findUserByPhoneRequest) {
        return Response.success(userService.findByPhone(findUserByPhoneRequest));
    }

    @PostMapping("/findById")
    @ApiOperationLog(description = "查询用户信息")
    public Response<FindUserByIdResponse> findById(@Validated @RequestBody UserIdRequest userIdRequest) {
        return userService.findById(userIdRequest);
    }

    @PostMapping("/findByIds")
    @ApiOperationLog(description = "批量查询用户信息")
    public Response<List<FindUserByIdResponse>> findByIds(@Validated @RequestBody FindUsersByIdsReqDTO findUsersByIdsReqDTO) {
        return userService.findByIds(findUsersByIdsReqDTO);
    }

    @PostMapping("/userInfo")
    @ApiOperationLog(description = "查询当前用户登录信息")
//    @PreAuthorize
    public Response<LoginUserInfoResponse> getLoginUserInfo() {
        return userService.getLoginUserInfo();
    }

}
