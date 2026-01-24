package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.convert.PictureConvert;
import com.xzf.blog.article.biz.domain.dataobject.AdvertisementPictureDO;
import com.xzf.blog.article.biz.domain.mapper.AdvertisementPictureMapper;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.biz.service.BlogSettingService;
import com.xzf.blog.article.dto.request.settings.AdvertisementPictureItem;
import com.xzf.blog.article.dto.request.settings.EditAdvertisementPictureRequest;
import com.xzf.blog.article.dto.response.settings.PicturePageListRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BlogSettingServiceImpl implements BlogSettingService {

    @Autowired
    private AdvertisementPictureMapper advertisementPictureMapper;

    @Value("${advertisement.picture.limit:5}")
    private Integer pictureLimit;


    @Override
    public PageResponse<PicturePageListRspVO> findAdvertisementPicturePageList(BasePageQuery req) {
        Long current = req.getCurrent();
        Long size = req.getSize();

        Page<AdvertisementPictureDO> pages = advertisementPictureMapper.selectPageList(current, size);
        List<AdvertisementPictureDO> pictures = pages.getRecords();

        List<PicturePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(pictures)) {
            vos= pictures.stream()
                    .map(PictureConvert.INSTANCE::convertDO2VO)
                    .toList();
        }
        return PageResponse.success(pages, vos);
    }

    @Override
    public Response<?> deleteAdvertisementPicture(Long id) {
        advertisementPictureMapper.deleteById(id);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> editAdvertisementPicture(EditAdvertisementPictureRequest req) {
        List<AdvertisementPictureItem> items = req.getItems();
        if(items.size()>pictureLimit){
            throw new BizException(BizResponseCodeEnum.EXCEED_PICTURE_LIMIT);
        }

        List<AdvertisementPictureDO> advertisementPictureDOS = items.stream()
                .map(PictureConvert.INSTANCE::convertItem2DO)
                .toList();
        // 全部删除后全部重新插入
        advertisementPictureMapper.delete(Wrappers.emptyWrapper());
        if(!items.isEmpty()){
            advertisementPictureMapper.insertBatch(advertisementPictureDOS);
        }

        return Response.success();
    }
}
