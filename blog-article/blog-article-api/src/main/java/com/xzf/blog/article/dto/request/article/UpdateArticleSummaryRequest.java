package com.xzf.blog.article.dto.request.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateArticleSummaryRequest {

    @NotNull(message = "文章 ID 不能为空")
    private Long id;

    @NotBlank(message = "文章摘要不能为空")
    private String summary;

}
