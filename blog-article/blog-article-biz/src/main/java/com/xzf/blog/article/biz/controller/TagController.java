package com.xzf.blog.article.biz.controller;

import com.xzf.framework.biz.operationlog.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 标签
 **/
@RestController
@RequestMapping("/tag")
public class TagController {

//    @Autowired
//    private TagService tagService;
//
//    @PostMapping("/list")
//    @ApiOperation(value = "前台获取标签列表")
//    @ApiOperationLog(description = "前台获取标签列表")
//    public Response findTagList(@RequestBody @Validated FindTagListReqVO findTagListReqVO) {
//        return tagService.findTagList(findTagListReqVO);
//    }
//
//    @PostMapping("/article/list")
//    @ApiOperation(value = "前台获取标签下文章列表")
//    @ApiOperationLog(description = "前台获取标签下文章列表")
//    public Response findTagPageList(@RequestBody @Validated FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
//        return tagService.findTagPageList(findTagArticlePageListReqVO);
//    }

}
