package com.xzf.blog.article.biz.controller;

import com.xzf.blog.article.biz.service.CategoryService;
import com.xzf.blog.article.dto.request.category.AddCategoryReqVO;
import com.xzf.blog.article.dto.request.category.DeleteCategoryReqVO;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
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
 * @description: 分类
 **/
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    @ApiOperationLog(description = "分类分页数据获取")
    public PageResponse findCategoryPageList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryPageList(findCategoryPageListReqVO);
    }

    @PostMapping("/select/list")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    public Response<List<SelectRspVO>> findCategorySelectList() {
        return categoryService.findCategorySelectList();
    }

    /*==================================  管理系统接口  ====================================*/

    @PostMapping("/add")
    @ApiOperationLog(description = "添加分类")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除分类")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return categoryService.deleteCategory(deleteCategoryReqVO);
    }

}
