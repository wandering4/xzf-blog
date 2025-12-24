package com.xzf.blog.oss.enums;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {

    PICTURE_ERROR("OSS-10002", "图片异常"),
    PICTURE_TOO_BIG("OSS-10003", "图片过大，无法上传"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}