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
@TableName("article")
public class ArticleDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String cover;

    private Long authorId;

    private Byte status;

    private Integer viewCount;

    private Integer commentCount;

    private String summary;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}