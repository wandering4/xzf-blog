package com.xzf.blog.user.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("role")
public class RoleDO extends BaseDO {

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色唯一标识
     */
    private String roleKey;

    /**
     * 状态(0：启用 1：禁用)
     */
    private Byte status;

    /**
     * 管理系统中的显示顺序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String desc;

}