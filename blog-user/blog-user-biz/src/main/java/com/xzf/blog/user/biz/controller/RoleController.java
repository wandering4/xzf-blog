package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.model.vo.request.SendVerificationCodeReqVO;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @PostMapping("/getAll")
    @ApiOperationLog(description = "获取所有角色信息")
    public Response<?> getAll() {
        List<String> roles = Arrays.stream(RoleEnums.values()).map(Enum::name).toList();
        return Response.success(roles);
    }

}
