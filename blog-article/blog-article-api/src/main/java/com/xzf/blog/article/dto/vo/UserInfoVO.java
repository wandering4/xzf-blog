package com.xzf.blog.article.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO {
    private Long id;
    private String userName;
    private String avatarUrl;
}
