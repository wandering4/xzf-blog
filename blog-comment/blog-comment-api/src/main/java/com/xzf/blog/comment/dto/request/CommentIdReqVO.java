package com.xzf.blog.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 评论id
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentIdReqVO {

    @NotNull(message = "评论 ID 不能为空")
    private Long id;
}
