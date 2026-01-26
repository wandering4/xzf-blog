package com.xzf.blog.comment.biz.convert;

import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;
import com.xzf.blog.comment.dto.response.FindCommentPageListVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @description: 评论实体类转换
 **/
@Mapper
public interface CommentConvert {
    /**
     * 初始化 convert 实例
     */
    CommentConvert INSTANCE = Mappers.getMapper(CommentConvert.class);

}
