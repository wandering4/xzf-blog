package com.xzf.blog.article.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleIsTopEnum {
    GENERAL(0, "不置顶"),
    TOP(1, "置顶");
    private int code;
    private String desc;
}
