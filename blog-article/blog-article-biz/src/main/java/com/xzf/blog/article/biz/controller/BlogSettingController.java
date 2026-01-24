package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.biz.service.BlogSettingService;
import com.xzf.blog.article.dto.request.IdRequest;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.request.settings.EditAdvertisementPictureRequest;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.article.dto.response.settings.PicturePageListRspVO;
import com.xzf.blog.framework.commons.request.BasePageQuery;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.context.aspect.PreAuthorize;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/blog/settings")
public class BlogSettingController {

    @Autowired
    private BlogSettingService blogSettingService;

    @PostMapping("/advertisement/picture/list")
    @ApiOperationLog(description = "博客广告图列表")
    public PageResponse<PicturePageListRspVO> findPicturePageList(@RequestBody BasePageQuery req) {
        return blogSettingService.findAdvertisementPicturePageList(req);
    }

    @PostMapping("/advertisement/picture/edit")
    @ApiOperationLog(description = "博客广告图编辑")
    @PreAuthorize(hasRoles = "root")
    public Response<?> editPicture(@RequestBody EditAdvertisementPictureRequest req) {
        return blogSettingService.editAdvertisementPicture(req);
    }

    @PostMapping("/advertisement/picture/delete")
    @ApiOperationLog(description = "博客广告图删除")
    @PreAuthorize(hasRoles = "root")
    public Response<?> deletePicture(@RequestBody IdRequest req) {
        return blogSettingService.deleteAdvertisementPicture(req.getId());
    }


}
