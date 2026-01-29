package com.xzf.blog.user.biz.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoleRequest {

    @NotNull(message = "用戶id不能为空")
    private Long userId;

    @NotNull(message = "角色id不能为空")
    private Long roleId;

}
