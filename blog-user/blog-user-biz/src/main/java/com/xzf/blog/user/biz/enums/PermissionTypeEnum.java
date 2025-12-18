package com.xzf.blog.user.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionTypeEnum {

    CONTENT(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮");

    private int code;
    private String desc;

}
