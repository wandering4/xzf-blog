package com.xzf.blog.article.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.domain.dataobject.AdvertisementPictureDO;

import java.util.List;

public interface AdvertisementPictureMapper extends BaseMapper<AdvertisementPictureDO> {
    default Page<AdvertisementPictureDO> selectPageList(Long current, Long size){
        // 分页对象
        Page<AdvertisementPictureDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<AdvertisementPictureDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AdvertisementPictureDO::getCreateTime); // order by create_time desc

        return selectPage(page, wrapper);
    }

    void insertBatch(List<AdvertisementPictureDO> advertisementPictureDOList);
}
