package com.xzf.blog.comment.biz.exception;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {
    NOT_HAVE_PERMISSION("COMMENT-20001", "没有权限删除"),
    COMMENT_NOT_FOUND("20017", "该评论不存在"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
