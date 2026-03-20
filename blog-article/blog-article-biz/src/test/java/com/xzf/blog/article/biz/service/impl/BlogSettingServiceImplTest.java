package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.domain.dataobject.AdvertisementPictureDO;
import com.xzf.blog.article.biz.domain.mapper.AdvertisementPictureMapper;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.dto.request.settings.AdvertisementPictureItem;
import com.xzf.blog.article.dto.request.settings.EditAdvertisementPictureRequest;
import com.xzf.blog.article.dto.response.settings.PicturePageListRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.request.BasePageQuery;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogSettingServiceImplTest {

    @Mock
    private AdvertisementPictureMapper advertisementPictureMapper;

    private BlogSettingServiceImpl blogSettingService;

    @BeforeEach
    void setUp() {
        blogSettingService = new BlogSettingServiceImpl();
        ReflectionTestUtils.setField(blogSettingService, "advertisementPictureMapper", advertisementPictureMapper);
        ReflectionTestUtils.setField(blogSettingService, "pictureLimit", 2);
    }

    @Test
    void shouldBuildPicturePageList() {
        AdvertisementPictureDO picture = AdvertisementPictureDO.builder()
                .id(1L)
                .url("https://img/p1.png")
                .sortOrder(1)
                .createTime(LocalDateTime.now())
                .build();
        Page<AdvertisementPictureDO> page = new Page<>(1, 10);
        page.setRecords(List.of(picture));
        page.setTotal(1L);
        when(advertisementPictureMapper.selectPageList(1L, 10L)).thenReturn(page);

        PageResponse<PicturePageListRspVO> response = blogSettingService.findAdvertisementPicturePageList(
                BasePageQuery.builder().current(1L).size(10L).build());

        assertTrue(response.isSuccess());
        assertEquals(1L, response.getTotalCount());
        assertEquals("https://img/p1.png", response.getData().get(0).getUrl());
    }

    @Test
    void shouldReturnNullDataWhenPicturePageIsEmpty() {
        Page<AdvertisementPictureDO> page = new Page<>(1, 10);
        page.setRecords(List.of());
        page.setTotal(0L);
        when(advertisementPictureMapper.selectPageList(1L, 10L)).thenReturn(page);

        PageResponse<PicturePageListRspVO> response = blogSettingService.findAdvertisementPicturePageList(
                BasePageQuery.builder().current(1L).size(10L).build());

        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }

    @Test
    void shouldDeleteAdvertisementPicture() {
        Response<?> response = blogSettingService.deleteAdvertisementPicture(11L);

        assertTrue(response.isSuccess());
        verify(advertisementPictureMapper).deleteById(11L);
    }

    @Test
    void shouldThrowWhenAdvertisementPicturesExceedLimit() {
        EditAdvertisementPictureRequest request = EditAdvertisementPictureRequest.builder()
                .items(List.of(
                        AdvertisementPictureItem.builder().url("u1").sortOrder(1).build(),
                        AdvertisementPictureItem.builder().url("u2").sortOrder(2).build(),
                        AdvertisementPictureItem.builder().url("u3").sortOrder(3).build()
                ))
                .build();

        BizException ex = assertThrows(BizException.class, () -> blogSettingService.editAdvertisementPicture(request));

        assertEquals(BizResponseCodeEnum.EXCEED_PICTURE_LIMIT.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void shouldDeleteAndInsertWhenEditingPictures() {
        EditAdvertisementPictureRequest request = EditAdvertisementPictureRequest.builder()
                .items(List.of(
                        AdvertisementPictureItem.builder().url("u1").sortOrder(1).build(),
                        AdvertisementPictureItem.builder().url("u2").sortOrder(2).build()
                ))
                .build();

        Response<?> response = blogSettingService.editAdvertisementPicture(request);

        assertTrue(response.isSuccess());
        verify(advertisementPictureMapper).delete(any());
        verify(advertisementPictureMapper).insertBatch(any(List.class));
    }

    @Test
    void shouldOnlyDeleteWhenEditingWithEmptyPictures() {
        EditAdvertisementPictureRequest request = EditAdvertisementPictureRequest.builder().items(List.of()).build();

        Response<?> response = blogSettingService.editAdvertisementPicture(request);

        assertTrue(response.isSuccess());
        verify(advertisementPictureMapper).delete(any());
        verify(advertisementPictureMapper, never()).insertBatch(any(List.class));
    }
}
