package com.xzf.blog.article.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleIsTopEnum {

    NOT_PINNED(0, "不置頂"),
    PINNED(1, "置頂");
    private int code;
    private String desc;

    public static ArticleIsTopEnum valueOf(boolean isTop) {
        return isTop ? PINNED : NOT_PINNED;
    }

}
