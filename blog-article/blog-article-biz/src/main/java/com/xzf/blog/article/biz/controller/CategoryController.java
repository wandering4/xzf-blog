package com.xzf.blog.article.biz.controller;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 分类
 **/
@RestController
@RequestMapping("/category")
public class CategoryController {

//    @Autowired
//    private CategoryService categoryService;
//
//    @PostMapping("/list")
//    @ApiOperationLog(description = "前台获取分类列表")
//    public Response<?> findCategoryList(@RequestBody @Validated FindCategoryListReqVO findCategoryListReqVO) {
//        return categoryService.findCategoryList(findCategoryListReqVO);
//    }
//
//    @PostMapping("/article/list")
//    @ApiOperationLog(description = "前台获取分类下文章分页数据")
//    public Response<?> findCategoryArticlePageList(@RequestBody @Validated FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
//        return categoryService.findCategoryArticlePageList(findCategoryArticlePageListReqVO);
//    }

}
