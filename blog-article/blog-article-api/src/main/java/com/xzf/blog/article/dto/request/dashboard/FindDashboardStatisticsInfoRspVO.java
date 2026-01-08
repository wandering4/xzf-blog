package com.xzf.blog.article.dto.request.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindDashboardStatisticsInfoRspVO {

    /**
     * 文章总数
     */
    private Long articleTotalCount;

    /**
     * 总浏览量
     */
    private Long pvTotalCount;
}
