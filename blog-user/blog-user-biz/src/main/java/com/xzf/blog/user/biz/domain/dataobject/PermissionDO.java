package com.xzf.blog.user.biz.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xzf.blog.framework.commons.domain.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("permission")
public class PermissionDO extends BaseDO {

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 类型(1：目录 2：菜单 3：按钮)
     */
    private Byte type;

    /**
     * 菜单路由
     */
    private String menuUrl;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 管理系统中的显示顺序
     */
    private Integer sort;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 状态(0：启用；1：禁用)
     */
    private Byte status;

}