package com.xzf.blog.user.biz.domain.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.dataobject.UserRoleDO;
import com.xzf.blog.user.biz.domain.mapper.RoleDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserRoleDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleManager {

    @Autowired
    private RoleDOMapper roleDOMapper;

    @Autowired
    private UserRoleDOMapper userRoleDOMapper;

    public RoleDO selectByUserId(Long userId) {
        LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleDO::getUserId, userId);
        UserRoleDO userRoleDO = userRoleDOMapper.selectOne(queryWrapper);
        if(userRoleDO == null) {
            return null;
        }
        LambdaQueryWrapper<RoleDO> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(RoleDO::getId,userRoleDO.getRoleId());
        return roleDOMapper.selectOne(roleQueryWrapper);
    }

}
