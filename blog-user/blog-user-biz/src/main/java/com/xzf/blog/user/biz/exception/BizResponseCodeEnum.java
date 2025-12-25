package com.xzf.blog.user.biz.exception;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {
    NICK_NAME_VALID_FAIL("USER-20001", "昵称请设置2-24个字符，不能使用@《/等特殊字符"),
    ACCOUNT_ID_VALID_FAIL("USER-20002", "账号请设置6-15个字符，仅可使用英文（必须）、数字、下划线"),
    SEX_VALID_FAIL("USER-20003", "性别错误"),
    INTRODUCTION_VALID_FAIL("USER-20004", "个人简介请设置1-100个字符"),
    UPLOAD_AVATAR_FAIL("USER-20005", "头像上传失败"),
    USER_NOT_FOUND("USER-20006", "该用户不存在"),
    CANT_UPDATE_OTHER_USER_PROFILE("USER-20007", "无权限修改用户信息"),
    PICTURE_TOO_BIG("USER-20008", "图片过大，无法上传"),
    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "请求太频繁，请3分钟后再试"),
    VERIFICATION_CODE_ERROR("AUTH-20001", "验证码错误"),
    LOGIN_FAILURE("AUTH-20002","登录失败"),
    LOGIN_TYPE_ERROR("AUTH-20002", "登录类型错误"),
    PHONE_OR_PASSWORD_ERROR("AUTH-20004", "手机号或密码错误"),
    LOGIN_FAIL("AUTH-20005", "登录失败"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
