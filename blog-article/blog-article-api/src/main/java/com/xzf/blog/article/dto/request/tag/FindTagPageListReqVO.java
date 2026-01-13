package com.xzf.blog.article.dto.request.tag;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @description: 标签分页
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagPageListReqVO extends BasePageQuery {

    /**
     * 标签名称
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
