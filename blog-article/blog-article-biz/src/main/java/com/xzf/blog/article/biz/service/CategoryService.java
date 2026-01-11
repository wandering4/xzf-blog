package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.request.category.AddCategoryReqVO;
import com.xzf.blog.article.dto.request.category.DeleteCategoryReqVO;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.article.dto.response.category.FindCategoryPageListRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;

import java.util.List;

public interface CategoryService {
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据查询
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse<FindCategoryPageListRspVO> findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response<?> deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的 Select 列表数据
     * @return
     */
    Response<List<SelectRspVO>> findCategorySelectList();
}
