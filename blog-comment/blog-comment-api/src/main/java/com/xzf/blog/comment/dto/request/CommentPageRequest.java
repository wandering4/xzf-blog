package com.xzf.blog.comment.dto.request;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommentPageRequest extends BasePageQuery {
    private Long articleId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
