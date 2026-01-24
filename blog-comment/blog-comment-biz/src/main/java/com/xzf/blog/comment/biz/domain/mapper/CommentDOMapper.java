package com.xzf.blog.comment.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public interface CommentDOMapper extends BaseMapper<CommentDO> {


    default Page<CommentDO> selectPageList(Long current, Long size, Long articleId, Long userId, LocalDate startDate, LocalDate endDate) {
        // 分页对象(查询第几页、每页多少数据)
        Page<CommentDO> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .eq(Objects.nonNull(articleId), CommentDO::getArticleId, articleId)
                .eq(Objects.nonNull(userId), CommentDO::getUserId, userId)
                .between(Objects.nonNull(startDate) && Objects.nonNull(endDate), CommentDO::getCreateTime, startDate, endDate)
                .orderByDesc(CommentDO::getCreateTime); // 按创建时间倒叙
        return selectPage(page, wrapper);
    }

    @MapKey("count")
    Map<Long, Long> count(@Param("articleIds") List<Long> articleIds);

    default int deleteByArticleId(Long articleId){
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery();
        wrapper.eq(CommentDO::getArticleId, articleId);
        return delete(wrapper);
    }

}