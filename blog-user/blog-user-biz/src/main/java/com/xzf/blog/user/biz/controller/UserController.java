package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.FindUserProfileReqVO;
import com.xzf.blog.user.biz.model.vo.request.UpdatePasswordRequest;
import com.xzf.blog.user.biz.model.vo.request.UpdateUserInfoRequest;
import com.xzf.blog.user.biz.model.vo.response.FindUserProfileRspVO;
import com.xzf.blog.user.biz.service.UserService;
import com.xzf.blog.user.dto.req.*;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
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
    public Response<?> updateUserInfo(@Validated UpdateUserInfoRequest updateUserInfoRequest) {
        return userService.updateUserInfo(updateUserInfoRequest);
    }

    /**
     * 获取用户详细信息
     *
     * @return
     */
    @PostMapping(value = "/profile")
    @ApiOperationLog(description = "获取用户详细信息")
    public Response<FindUserProfileRspVO> findUserProfile(@Validated @RequestBody FindUserProfileReqVO findUserProfileReqVO) {
        return userService.findUserProfile(findUserProfileReqVO);
    }


    // ===================================== 对其他服务提供的接口 =====================================


    @PostMapping("/findByPhone")
    @ApiOperationLog(description = "手机号查询用户信息")
    public Response<FindUserByPhoneRspDTO> findByPhone(@Validated @RequestBody FindUserByPhoneRequest findUserByPhoneRequest) {
        return Response.success(userService.findByPhone(findUserByPhoneRequest));
    }


    @PostMapping("/findById")
    @ApiOperationLog(description = "查询用户信息")
    public Response<FindUserByIdResponse> findById(@Validated @RequestBody FindUserByIdRequest findUserByIdRequest) {
        return userService.findById(findUserByIdRequest);
    }

    @PostMapping("/findByIds")
    @ApiOperationLog(description = "批量查询用户信息")
    public Response<List<FindUserByIdResponse>> findByIds(@Validated @RequestBody FindUsersByIdsReqDTO findUsersByIdsReqDTO) {
        return userService.findByIds(findUsersByIdsReqDTO);
    }

}
