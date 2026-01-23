package com.xzf.blog.user.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserInfoResponse {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色
     */
    private String role;

    private String userName;

    /**
     * 头像
     */
    private String avatarUrl;
}
