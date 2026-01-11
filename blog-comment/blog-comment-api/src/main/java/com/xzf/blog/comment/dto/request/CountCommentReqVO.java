package com.xzf.blog.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountCommentReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private List<Long> articleIdList;
}
