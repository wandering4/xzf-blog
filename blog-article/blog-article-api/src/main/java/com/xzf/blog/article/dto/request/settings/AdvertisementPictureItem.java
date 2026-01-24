package com.xzf.blog.article.dto.request.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementPictureItem {

    @NotBlank(message = "图片url不能为空")
    private String url;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

}
