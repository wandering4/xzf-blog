package com.xzf.blog.article.biz.exception;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizResponseCodeEnum implements BaseExceptionInterface {
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    USERNAME_NOT_FOUND("20003", "该用户不存在"),
    FORBIDDEN("20004", "演示账号仅支持查询操作！"),
    CATEGORY_NAME_IS_EXISTED("20005", "该分类已存在，请勿重复添加！"),
    TAG_CANT_DUPLICATE("20006", "请勿添加表中已存在的标签！"),
    TAG_NOT_EXISTED("20007", "该标签不存在！"),
    TAG_EXCEED_LIMIT("20008", "添加标签数超过上限！"),
    FILE_UPLOAD_FAILED("20009", "文件上传失败！"),
    CATEGORY_NOT_EXISTED("20010", "提交的分类不存在！"),
    ARTICLE_NOT_FOUND("20011", "该文章不存在！"),
    CATEGORY_CAN_NOT_DELETE("20012", "该分类下包含文章，请先删除对应文章，才能删除！"),
    TAG_CAN_NOT_DELETE("20013", "该标签下包含文章，请先删除对应文章，才能删除！"),
    EXCEED_PICTURE_LIMIT("20014", "超过最大广告图片可配置数"),

    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
