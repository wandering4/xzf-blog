package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.domain.dataobject.CategoryDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleCategoryMapper;
import com.xzf.blog.article.biz.domain.mapper.CategoryMapper;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.dto.request.category.AddCategoryReqVO;
import com.xzf.blog.article.dto.request.category.DeleteCategoryReqVO;
import com.xzf.blog.article.dto.request.category.FindCategoryPageListReqVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.article.dto.response.category.FindCategoryPageListRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private ArticleCategoryMapper articleCategoryMapper;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl();
        ReflectionTestUtils.setField(categoryService, "categoryMapper", categoryMapper);
        ReflectionTestUtils.setField(categoryService, "articleCategoryMapper", articleCategoryMapper);
    }

    @Test
    void shouldThrowWhenCategoryNameExists() {
        when(categoryMapper.selectOne(any())).thenReturn(CategoryDO.builder().id(1L).name("tech").build());

        BizException ex = assertThrows(BizException.class,
                () -> categoryService.addCategory(AddCategoryReqVO.builder().name("tech").build()));

        assertEquals(BizResponseCodeEnum.CATEGORY_NAME_IS_EXISTED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void shouldInsertCategoryWhenNameNotExists() {
        when(categoryMapper.selectOne(any())).thenReturn(null);

        Response<String> response = categoryService.addCategory(AddCategoryReqVO.builder().name("tech").build());

        assertTrue(response.isSuccess());
        verify(categoryMapper).insert(any(CategoryDO.class));
    }

    @Test
    void shouldReturnFailWhenCategoryNotFoundOnDelete() {
        when(categoryMapper.selectById(8L)).thenReturn(null);

        Response<?> response = categoryService.deleteCategory(DeleteCategoryReqVO.builder().id(8L).build());

        assertFalse(response.isSuccess());
    }

    @Test
    void shouldThrowWhenCategoryHasArticlesOnDelete() {
        when(categoryMapper.selectById(9L)).thenReturn(CategoryDO.builder().id(9L).name("java").build());
        when(articleCategoryMapper.countArticlesByCategoryId(9L)).thenReturn(3);

        BizException ex = assertThrows(BizException.class,
                () -> categoryService.deleteCategory(DeleteCategoryReqVO.builder().id(9L).build()));

        assertEquals(BizResponseCodeEnum.CATEGORY_CAN_NOT_DELETE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void shouldDeleteCategoryWhenNoArticleReferences() {
        when(categoryMapper.selectById(10L)).thenReturn(CategoryDO.builder().id(10L).name("ops").build());
        when(articleCategoryMapper.countArticlesByCategoryId(10L)).thenReturn(0);

        Response<?> response = categoryService.deleteCategory(DeleteCategoryReqVO.builder().id(10L).build());

        assertTrue(response.isSuccess());
        verify(categoryMapper).deleteById(10L);
    }

    @Test
    void shouldBuildCategoryPageListResponse() {
        CategoryDO category = CategoryDO.builder()
                .id(100L)
                .name("spring")
                .createTime(LocalDateTime.now())
                .build();
        Page<CategoryDO> page = new Page<>(1, 10);
        page.setRecords(List.of(category));
        page.setTotal(1L);
        when(categoryMapper.selectPage(any(Page.class), any())).thenReturn(page);
        when(articleCategoryMapper.countArticlesByCategoryId(100L)).thenReturn(4);

        PageResponse<FindCategoryPageListRspVO> response = categoryService.findCategoryPageList(
                FindCategoryPageListReqVO.builder().current(1L).size(10L).name("spr").build());

        assertTrue(response.isSuccess());
        assertEquals(1L, response.getTotalCount());
        assertEquals(1, response.getData().size());
        assertEquals(4, response.getData().get(0).getArticlesTotal());
    }

    @Test
    void shouldMapCategoriesToSelectResponse() {
        when(categoryMapper.selectList(null)).thenReturn(List.of(
                CategoryDO.builder().id(1L).name("java").build(),
                CategoryDO.builder().id(2L).name("ai").build()
        ));

        Response<List<SelectRspVO>> response = categoryService.findCategorySelectList();

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        assertEquals("java", response.getData().get(0).getLabel());
        assertEquals(2L, response.getData().get(1).getValue());
    }

    @Test
    void shouldReturnNullDataWhenNoCategoriesForSelect() {
        when(categoryMapper.selectList(null)).thenReturn(List.of());

        Response<List<SelectRspVO>> response = categoryService.findCategorySelectList();

        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }
}
