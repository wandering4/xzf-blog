package com.xzf.blog.user.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDOMapper extends BaseMapper<UserDO> {

    /**
     * 批量查询用户信息
     *
     * @param ids
     * @return
     */
    List<UserDO> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据手机号查询记录
     * @param phone
     * @return
     */
    UserDO selectByPhone(@Param("phone") String phone);

}