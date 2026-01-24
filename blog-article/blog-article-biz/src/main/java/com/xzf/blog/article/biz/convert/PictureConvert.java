package com.xzf.blog.article.biz.convert;

import com.xzf.blog.article.biz.domain.dataobject.AdvertisementPictureDO;
import com.xzf.blog.article.dto.request.settings.AdvertisementPictureItem;
import com.xzf.blog.article.dto.response.settings.PicturePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @description: 广告图片实体类转换
 **/
@Mapper
public interface PictureConvert {
    /**
     * 初始化 convert 实例
     */
    PictureConvert INSTANCE = Mappers.getMapper(PictureConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    PicturePageListRspVO convertDO2VO(AdvertisementPictureDO bean);


    AdvertisementPictureDO convertItem2DO(AdvertisementPictureItem bean);
}
