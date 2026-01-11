package com.xzf.blog.article.biz.service.impl;

import com.xzf.blog.article.biz.domain.dataobject.CategoryDO;
import com.xzf.blog.article.biz.domain.mapper.CategoryMapper;
import com.xzf.blog.article.biz.service.CategoryService;
import com.xzf.blog.article.dto.request.category.AddCategoryReqVO;
import com.xzf.blog.article.dto.request.category.DeleteCategoryReqVO;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.article.dto.response.category.FindCategoryPageListRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        return null;
    }

    @Override
    public PageResponse<FindCategoryPageListRspVO> findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return null;
    }

    @Override
    public Response<?> deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        return null;
    }

    /**
     * 获取文章分类的 Select 列表数据
     *
     * @return
     */
    @Override
    public Response<List<SelectRspVO>> findCategorySelectList() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;
        // 如果分类数据不为空
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 将分类 ID 作为 Value 值，将分类名称作为 label 展示
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }
}
