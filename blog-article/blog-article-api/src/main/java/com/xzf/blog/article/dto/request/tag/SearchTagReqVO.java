package com.xzf.blog.article.dto.request.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description: 标签搜索
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTagReqVO {
    private String tagName;
}
