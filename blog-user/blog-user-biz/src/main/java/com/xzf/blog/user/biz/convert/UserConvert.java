package com.xzf.blog.user.biz.convert;

import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import com.xzf.blog.user.dto.resp.FindUserPageListRspVO;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.Date;

public class UserConvert {
    public static FindUserPageListRspVO userDOtoPageVO(UserDO userDO, RoleDO roleDO) {
        FindUserPageListRspVO rspVO = new FindUserPageListRspVO();
        BeanUtils.copyProperties(userDO, rspVO);
        rspVO.setRole(roleDO.getRoleName());
        rspVO.setCreateDate(new Date(Timestamp.valueOf(userDO.getCreateTime()).getTime()));
        return rspVO;
    }
}
