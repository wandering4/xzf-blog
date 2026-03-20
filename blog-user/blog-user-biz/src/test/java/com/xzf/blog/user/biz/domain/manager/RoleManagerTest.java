package com.xzf.blog.user.biz.domain.manager;

import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.dataobject.UserRoleDO;
import com.xzf.blog.user.biz.domain.mapper.RoleDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserRoleDOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleManagerTest {

    private RoleManager roleManager;

    @Mock
    private RoleDOMapper roleDOMapper;
    @Mock
    private UserRoleDOMapper userRoleDOMapper;

    @BeforeEach
    void setUp() {
        roleManager = new RoleManager();
        ReflectionTestUtils.setField(roleManager, "roleDOMapper", roleDOMapper);
        ReflectionTestUtils.setField(roleManager, "userRoleDOMapper", userRoleDOMapper);
    }

    @Test
    void selectByUserIdShouldReturnNullWhenUserHasNoRole() {
        when(userRoleDOMapper.selectOne(any())).thenReturn(null);

        assertThat(roleManager.selectByUserId(1L)).isNull();
    }

    @Test
    void selectByUserIdShouldReturnRole() {
        when(userRoleDOMapper.selectOne(any())).thenReturn(UserRoleDO.builder().userId(1L).roleId(2L).build());
        when(roleDOMapper.selectOne(any())).thenReturn(RoleDO.builder().id(2L).roleKey("admin").build());

        RoleDO role = roleManager.selectByUserId(1L);

        assertThat(role).isNotNull();
        assertThat(role.getRoleKey()).isEqualTo("admin");
    }
}
