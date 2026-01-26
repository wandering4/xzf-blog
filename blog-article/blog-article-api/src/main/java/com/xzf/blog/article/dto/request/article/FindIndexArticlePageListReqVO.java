package com.xzf.blog.article.dto.request.article;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FindIndexArticlePageListReqVO extends BasePageQuery {
    private List<Long> tagIds;
    private List<Long> categoryIds;
    private Long userId;
    private String title;
    /**
     * 发布的起始日期
     */
    private LocalDate startCreateTime;

    /**
     * 发布的结束日期
     */
    private LocalDate endCreateTime;
}

