package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.article.biz.domain.dataobject.ArticleTagDO;
import com.xzf.blog.article.biz.domain.dataobject.TagDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleTagMapper;
import com.xzf.blog.article.biz.domain.mapper.TagMapper;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.dto.request.tag.AddTagReqVO;
import com.xzf.blog.article.dto.request.tag.DeleteTagReqVO;
import com.xzf.blog.article.dto.request.tag.FindTagPageListReqVO;
import com.xzf.blog.article.dto.request.tag.SearchTagReqVO;
import com.xzf.blog.article.dto.request.tag.FindTagPageListRspVO;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagMapper tagMapper;
    @Mock
    private ArticleTagMapper articleTagMapper;

    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        tagService = spy(new TagServiceImpl());
        ReflectionTestUtils.setField(tagService, "tagMapper", tagMapper);
        ReflectionTestUtils.setField(tagService, "articleTagRelMapper", articleTagMapper);
        ReflectionTestUtils.setField(tagService, "limit", 20);
    }

    @Test
    void shouldThrowWhenTagLimitExceeded() {
        when(tagMapper.selectCount(any())).thenReturn(20L);

        BizException ex = assertThrows(BizException.class,
                () -> tagService.addTags(AddTagReqVO.builder().tags(List.of("a")).build()));

        assertEquals(BizResponseCodeEnum.TAG_EXCEED_LIMIT.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void shouldTrimTagNamesAndSaveBatch() {
        when(tagMapper.selectCount(any())).thenReturn(0L);
        doReturn(true).when(tagService).saveBatch(any(Collection.class));

        Response<?> response = tagService.addTags(AddTagReqVO.builder().tags(List.of(" alpha ", " beta")).build());

        assertTrue(response.isSuccess());
        ArgumentCaptor<Collection<TagDO>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(tagService).saveBatch(captor.capture());
        List<String> names = captor.getValue().stream().map(TagDO::getName).toList();
        assertEquals(List.of("alpha", "beta"), names);
    }

    @Test
    void shouldThrowWhenDeletingTagThatStillHasArticles() {
        when(articleTagMapper.selectOneByTagId(7L)).thenReturn(ArticleTagDO.builder().id(1L).build());

        BizException ex = assertThrows(BizException.class,
                () -> tagService.deleteTag(DeleteTagReqVO.builder().id(7L).build()));

        assertEquals(BizResponseCodeEnum.TAG_CAN_NOT_DELETE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void shouldReturnFailWhenDeletingNonExistingTag() {
        when(articleTagMapper.selectOneByTagId(8L)).thenReturn(null);
        when(tagMapper.deleteById(8L)).thenReturn(0);

        Response<?> response = tagService.deleteTag(DeleteTagReqVO.builder().id(8L).build());

        assertFalse(response.isSuccess());
        assertEquals(BizResponseCodeEnum.TAG_NOT_EXISTED.getErrorCode(), response.getErrorCode());
    }

    @Test
    void shouldSearchTagsAndMapToSelectOptions() {
        TagDO t1 = TagDO.builder().id(1L).name("java").build();
        TagDO t2 = TagDO.builder().id(2L).name("spring").build();
        when(tagMapper.selectList(any())).thenReturn(List.of(t1, t2));

        Response<List<SelectRspVO>> response = tagService.searchTag(SearchTagReqVO.builder().tagName("sp").build());

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        assertEquals("java", response.getData().get(0).getLabel());
        assertEquals(2L, response.getData().get(1).getValue());
    }

    @Test
    void shouldBuildTagPageList() {
        TagDO tag = TagDO.builder().id(1L).name("java").createTime(LocalDateTime.now()).build();
        Page<TagDO> page = new Page<>(1, 10);
        page.setRecords(List.of(tag));
        page.setTotal(1L);
        when(tagMapper.selectPageList(1L, 10L, "ja", null, null)).thenReturn(page);
        when(articleTagMapper.countArticlesByTagId(1L)).thenReturn(5L);

        PageResponse<FindTagPageListRspVO> response = tagService.findTagPageList(
                FindTagPageListReqVO.builder().current(1L).size(10L).name("ja").build());

        assertTrue(response.isSuccess());
        assertEquals(1L, response.getTotalCount());
        assertEquals(1, response.getData().size());
        assertEquals(5L, response.getData().get(0).getArticlesTotal());
    }

    @Test
    void shouldReturnNullDataWhenNoTagsForSelectList() {
        when(tagMapper.selectList(any())).thenReturn(List.of());

        Response<List<SelectRspVO>> response = tagService.findTagSelectList();

        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }
}
