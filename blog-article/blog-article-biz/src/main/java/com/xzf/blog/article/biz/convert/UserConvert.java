package com.xzf.blog.article.biz.convert;

import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.article.dto.vo.UserInfoVO;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @description: 文章转换
 **/
@Mapper
public interface UserConvert {
    /**
     * 初始化 convert 实例
     */
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);


    UserInfoVO convertResp2VO(FindUserByIdResponse bean);


}
