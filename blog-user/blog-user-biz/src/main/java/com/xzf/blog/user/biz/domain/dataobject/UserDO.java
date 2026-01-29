package com.xzf.blog.user.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class UserDO extends BaseDO {

    /**
     * 用户名称
     */
    private String username;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 个人简介
     */
    private String introduction;

}