package com.xzf.blog.article.dto.request.article;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindIndexArticlePageListReqVO extends BasePageQuery {
    private Long tagId;
    private Long categoryId;
}

