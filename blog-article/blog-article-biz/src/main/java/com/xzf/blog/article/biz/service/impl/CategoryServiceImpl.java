package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.domain.dataobject.CategoryDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleCategoryMapper;
import com.xzf.blog.article.biz.domain.mapper.CategoryMapper;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.biz.service.CategoryService;
import com.xzf.blog.article.dto.request.category.AddCategoryReqVO;
import com.xzf.blog.article.dto.request.category.DeleteCategoryReqVO;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.article.dto.response.category.FindCategoryPageListRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public Response<String> addCategory(AddCategoryReqVO addCategoryReqVO) {
        // 检查分类名称是否已存在
        CategoryDO existingCategory = categoryMapper.selectOne(
            Wrappers.<CategoryDO>lambdaQuery()
                .eq(CategoryDO::getName, addCategoryReqVO.getName())
        );

        if (existingCategory != null) {
            log.warn("分类名称： {}, 此已存在", addCategoryReqVO.getName());
            throw new BizException(BizResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        // 创建新分类
        CategoryDO categoryDO = CategoryDO.builder()
            .name(addCategoryReqVO.getName())
            .createTime(LocalDateTime.now())
            .updateTime(LocalDateTime.now())
            .build();

        // 插入数据库
        categoryMapper.insert(categoryDO);

        return Response.success("分类添加成功");
    }

    @Override
    public PageResponse<FindCategoryPageListRspVO> findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        // 构建分页对象
        Page<CategoryDO> page = new Page<>(findCategoryPageListReqVO.getCurrent(), findCategoryPageListReqVO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = Wrappers.<CategoryDO>lambdaQuery()
                .like(StringUtils.isNotBlank(findCategoryPageListReqVO.getName()), CategoryDO::getName, findCategoryPageListReqVO.getName())
                .ge(findCategoryPageListReqVO.getStartDate() != null, CategoryDO::getCreateTime, findCategoryPageListReqVO.getStartDate())
                .le(findCategoryPageListReqVO.getEndDate() != null, CategoryDO::getCreateTime, findCategoryPageListReqVO.getEndDate())
                .orderByDesc(CategoryDO::getCreateTime);

        // 分页查询分类
        Page<CategoryDO> categoryPage = categoryMapper.selectPage(page, wrapper);

        // 转换为响应 VO
        List<FindCategoryPageListRspVO> rspVOList = categoryPage.getRecords().stream()
                .map(categoryDO -> {
                    // 统计该分类下的文章数量
                    int articlesTotal = articleCategoryMapper.countArticlesByCategoryId(categoryDO.getId());
                    return FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .articlesTotal(articlesTotal)
                            .build();
                })
                .collect(Collectors.toList());

        // 返回分页响应
        return PageResponse.success(categoryPage, rspVOList);
    }

    @Override
    public Response<?> deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        // 检查分类是否存在
        CategoryDO categoryDO = categoryMapper.selectById(deleteCategoryReqVO.getId());
        if (categoryDO == null) {
            return Response.fail("分类不存在");
        }

        // 检查该分类下是否有文章
        int articlesCount = articleCategoryMapper.countArticlesByCategoryId(deleteCategoryReqVO.getId());
        if (articlesCount > 0) {
            log.warn("==> 此分类下包含文章，无法删除，categoryId: {}", deleteCategoryReqVO.getId());
            throw new BizException(BizResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }

        // 删除分类
        categoryMapper.deleteById(deleteCategoryReqVO.getId());

        return Response.success("分类删除成功");
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
