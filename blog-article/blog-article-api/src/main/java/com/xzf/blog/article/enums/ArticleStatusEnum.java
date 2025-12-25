package com.xzf.blog.article.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleStatusEnum {
    DRAFT(0, "草稿"),
    ENABLE(1, "发布"),
    DISABLED(2, "下架"),
    DELETED(3, "删除");
    private int code;
    private String desc;
}
