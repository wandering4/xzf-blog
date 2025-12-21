package com.xzf.blog.user.biz.model.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date: 2024/4/7 15:17
 * @version: v1.0.0
 * @description: 获取用户主页信息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserProfileRspVO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 昵称
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 个人介绍
     */
    private String introduction;


}