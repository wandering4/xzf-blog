package com.xzf.blog.article.dto.request.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditAdvertisementPictureRequest {
    private List<AdvertisementPictureItem> items = new ArrayList<>();
}
