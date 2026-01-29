package com.xzf.blog.user.biz.exception;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {
    NICK_NAME_VALID_FAIL("USER-20001", "昵称请设置2-24个字符，不能使用@《/等特殊字符"),
    INTRODUCTION_VALID_FAIL("USER-20002", "个人简介请设置1-100个字符"),
    UPLOAD_AVATAR_FAIL("USER-20003", "头像上传失败"),
    USER_NOT_FOUND("USER-20004", "该用户不存在"),
    ROLE_NOT_FOUND("USER-20005", "该角色不存在"),
    CANT_UPDATE_OTHER_USER_PROFILE("USER-20006", "无权限修改用户信息"),
    PICTURE_TOO_BIG("USER-20007", "图片过大，无法上传"),
    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20001", "请求太频繁，请3分钟后再试"),
    VERIFICATION_PICTURE_NOT_EXIST("AUTH-20002", "图片验证码不存在或已过期"),
    VERIFICATION_PICTURE_GENERATE_FAIL("AUTH-20003", "图片验证码生成失败"),
    VERIFICATION_PICTURE_ERROR("AUTH-20004", "图片验证码错误"),
    VERIFICATION_CODE_ERROR("AUTH-20005", "手机验证码错误"),
    LOGIN_FAILURE("AUTH-20006","登录失败"),
    LOGIN_TYPE_ERROR("AUTH-20007", "登录类型错误"),
    PASSWORD_ERROR("AUTH-20008", "密码错误"),
    LOGIN_FAIL("AUTH-20009", "登录失败"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
