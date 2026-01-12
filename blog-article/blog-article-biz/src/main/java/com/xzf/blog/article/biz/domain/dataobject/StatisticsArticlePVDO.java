package com.xzf.blog.article.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("statistics_article_pv")
public class StatisticsArticlePVDO extends BaseDO {

    private LocalDate pvDate;

    private Long pvCount;

}