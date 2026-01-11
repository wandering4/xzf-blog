package com.xzf.blog.framework.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnums {

    ROOT(1L, "root","管理员"),
    COMMON_USER(10L, "common_user","普通用户");

    private Long id;
    private String name;
    private String desc;
}
