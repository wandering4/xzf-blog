package com.xzf.blog.user.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzf.blog.framework.commons.enums.StatusEnum;
import com.xzf.blog.user.biz.domain.dataobject.RoleDO;

import java.util.List;

public interface RoleDOMapper extends BaseMapper<RoleDO> {
    default List<RoleDO> selectEnabledList() {
        LambdaQueryWrapper<RoleDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleDO::getStatus, StatusEnum.ENABLE.getValue());
        return selectList(queryWrapper);
    }
}