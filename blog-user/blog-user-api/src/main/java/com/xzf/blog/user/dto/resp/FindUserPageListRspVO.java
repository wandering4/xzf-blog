package com.xzf.blog.user.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserPageListRspVO {

    private Long id;

    private String userName;

    private String avatarUrl;

    private String role;

}
