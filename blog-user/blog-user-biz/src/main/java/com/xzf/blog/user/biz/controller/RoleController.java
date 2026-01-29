package com.xzf.blog.user.biz.controller;

import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.dto.resp.RolesResponse;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @GetMapping("/getAll")
    @ApiOperationLog(description = "获取所有角色信息")
    public Response<?> getAll() {
        List<RolesResponse> roles = Arrays.stream(RoleEnums.values()).map(roleEnum ->
                RolesResponse.builder()
                        .id(roleEnum.getId())
                        .name(roleEnum.getName())
                        .desc(roleEnum.getDesc())
                        .build()
        ).toList();
        return Response.success(roles);
    }

}
