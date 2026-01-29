package com.xzf.blog.article.dto.request.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditAdvertisementPictureRequest {

    @Valid
    @Size(max = 5, message = "广告图片最多只能上传5张")
    private List<AdvertisementPictureItem> items = new ArrayList<>();

}
