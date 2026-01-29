package com.xzf.blog.article.biz.service;

import com.xzf.blog.article.dto.request.tag.*;
import com.xzf.blog.article.dto.response.SelectRspVO;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;

import java.util.List;

public interface TagService {

    /**
     * 添加标签集合
     * @param addTagReqVO
     * @return
     */
    Response addTags(AddTagReqVO addTagReqVO);

    /**
     * 查询标签分页
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse<FindTagPageListRspVO> findTagPageList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 查询标签 Select 列表数据
     * @return
     */
    Response<List<SelectRspVO>> findTagSelectList();

    Response<List<SelectRspVO>> searchTag(SearchTagReqVO req);
}
