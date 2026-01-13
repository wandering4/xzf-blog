package com.xzf.blog.article.dto.request.category;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @description: 分类分页
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryPageListReqVO extends BasePageQuery {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 创建的起始日期
     */
    private LocalDate startDate;

    /**
     * 创建的结束日期
     */
    private LocalDate endDate;

}
