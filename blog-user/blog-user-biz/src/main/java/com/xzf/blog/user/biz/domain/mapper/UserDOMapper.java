package com.xzf.blog.user.biz.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

public interface UserDOMapper extends BaseMapper<UserDO> {

    /**
     * 根据手机号查询记录
     * @param phone
     * @return
     */
    UserDO selectByPhone(@Param("phone") String phone);

    default Page<UserDO> selectPageList(Long current, Long size, String name, Long userId) {
        // 分页对象(查询第几页、每页多少数据)
        Page<UserDO> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.<UserDO>lambdaQuery()
                .like(Objects.nonNull(name) && !name.isEmpty(), UserDO::getUsername, name)
                .ne(UserDO::getId, userId)
                .orderByAsc(UserDO::getId); // 按创建时间倒叙
        return selectPage(page, wrapper);
    }
}