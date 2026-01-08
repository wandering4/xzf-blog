package com.xzf.blog.comment.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.comment.biz.domain.dataobject.CommentDO;


public interface CommentDOMapper extends BaseMapper<CommentDO> {

    /**
     * 分页查询
     * @param current
     * @param size
     * @return
     */
    default Page<CommentDO> selectPageList(Long current, Long size) {
        // 分页对象(查询第几页、每页多少数据)
        Page<CommentDO> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .orderByDesc(CommentDO::getCreateTime); // 按创建时间倒叙
        return selectPage(page, wrapper);
    }

}