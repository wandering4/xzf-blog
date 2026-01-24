package com.xzf.blog.article.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("advertisement_picture")
public class AdvertisementPictureDO extends BaseDO {
    private String url;

    private Integer sortOrder;
}
