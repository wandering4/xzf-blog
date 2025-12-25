package com.xzf.blog.article.dto.request.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailReqVO {
    /**
     * 文章 ID
     */
    private Long articleId;
}