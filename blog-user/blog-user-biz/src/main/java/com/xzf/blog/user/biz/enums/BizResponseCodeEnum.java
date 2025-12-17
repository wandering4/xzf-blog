package com.xzf.blog.user.biz.enums;

import com.xzf.blog.framework.commons.enums.ResponseCodeEnum;
import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {
    NICK_NAME_VALID_FAIL("USER-20001", "昵称请设置2-24个字符，不能使用@《/等特殊字符"),
    ACCOUNT_ID_VALID_FAIL("USER-20002", "账号请设置6-15个字符，仅可使用英文（必须）、数字、下划线"),
    UPLOAD_AVATAR_FAIL("USER-20003", "头像上传失败"),
    USER_NOT_FOUND("USER-20004", "该用户不存在"),
    CANT_UPDATE_OTHER_USER_PROFILE("USER-20005", "无权限修改用户信息"),
    PICTURE_TOO_BIG("USER-20006", "图片过大，无法上传"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
