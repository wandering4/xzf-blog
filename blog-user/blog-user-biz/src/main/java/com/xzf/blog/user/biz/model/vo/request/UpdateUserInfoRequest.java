package com.xzf.blog.user.biz.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @date: 2024/4/13 18:17
 * @version: v1.0.0
 * @description: 修改用户信息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserInfoRequest {

    @NotNull(message = "用户 ID 不能为空")
    private Long id;

    /**
     * 头像
     */
    private MultipartFile avatar;

    /**
     * 昵称
     */
    private String nickname;


    /**
     * 性别
     */
    private Integer sex;


    /**
     * 个人介绍
     */
    private String introduction;

}
