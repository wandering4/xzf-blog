package com.xzf.blog.article.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("article")
public class ArticleDO  extends BaseDO {

    private String title;

    private String cover;

    private Long authorId;

    private Byte status;

    private Long viewCount;

    private Integer isTop;

    private String summary;


}