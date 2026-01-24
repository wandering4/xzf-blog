package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.biz.service.CategoryService;
import com.xzf.blog.article.biz.service.TagService;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.request.tag.AddTagReqVO;
import com.xzf.blog.article.dto.request.tag.DeleteTagReqVO;
import com.xzf.blog.article.dto.request.tag.FindTagPageListReqVO;
import com.xzf.blog.article.dto.request.tag.SearchTagReqVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 标签
 **/
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/search")
    @ApiOperationLog(description = "搜索标签")
    public Response<List<SelectRspVO>> searchTag(@RequestBody @Validated SearchTagReqVO req) {
        return tagService.searchTag(req);
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "标签分页数据获取")
    public PageResponse findTagPageList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagPageList(findTagPageListReqVO);
    }

    @PostMapping("/select/list")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    public Response findTagSelectList() {
        return tagService.findTagSelectList();
    }

    /*==================================  管理系统接口  ====================================*/

    @PostMapping("/add")
    @ApiOperationLog(description = "添加标签")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addTags(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return tagService.addTags(addTagReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除标签")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO) {
        return tagService.deleteTag(deleteTagReqVO);
    }


}
