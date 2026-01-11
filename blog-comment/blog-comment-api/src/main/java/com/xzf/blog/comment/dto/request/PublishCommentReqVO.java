package com.xzf.blog.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @description: 发布评论
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishCommentReqVO {

    /**
     * 回复的文章 ID
     */
    private Long articleId;

    @NotBlank(message = "评论内容不能为空")
    @Length(min = 1, max = 120, message = "评论内容需大于 1 小于 120 字符")
    private String content;

}
