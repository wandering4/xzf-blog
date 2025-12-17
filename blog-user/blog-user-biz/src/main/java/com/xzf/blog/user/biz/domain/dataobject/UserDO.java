package com.xzf.blog.user.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDO {
    private Long id;

    private String username;

    private String avatarUrl;

    private Byte role;

    private String password;

    private String phone;

    private Date createTime;

    private Date updateTime;


}