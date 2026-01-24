package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.request.settings.EditAdvertisementPictureRequest;
import com.xzf.blog.article.dto.response.settings.PicturePageListRspVO;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;

public interface BlogSettingService {
    PageResponse<PicturePageListRspVO> findAdvertisementPicturePageList(BasePageQuery req);

    Response<?> deleteAdvertisementPicture(Long id);

    Response<?> editAdvertisementPicture(EditAdvertisementPictureRequest req);
}
