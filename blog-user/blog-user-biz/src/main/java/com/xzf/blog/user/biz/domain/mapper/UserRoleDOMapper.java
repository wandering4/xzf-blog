package com.xzf.blog.user.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzf.blog.user.biz.domain.dataobject.UserRoleDO;

public interface UserRoleDOMapper extends BaseMapper<UserRoleDO> {

    default void deleteByUserId(Long userId){
        LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleDO::getUserId, userId);
        delete(queryWrapper);
    }

}