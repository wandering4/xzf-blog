package com.xzf.blog.article.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("article_category_rel")
public class ArticleCategoryDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long categoryId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}