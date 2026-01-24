package com.xzf.blog.article.dto.response.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PicturePageListRspVO {
    private Long id;
    private String url;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
