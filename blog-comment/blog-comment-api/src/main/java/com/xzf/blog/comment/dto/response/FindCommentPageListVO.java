package com.xzf.blog.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentPageListVO {

    private Long id;

    private String avatarUrl;

    private String userName;

    private String articleName;

    private String content;

    private LocalDateTime createTime;

}

