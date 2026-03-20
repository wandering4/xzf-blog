package com.xzf.blog.gateway.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzf.blog.framework.commons.constant.RedisKeyConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StpInterfaceImplTest {

    private StpInterfaceImpl stpInterface;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        stpInterface = new StpInterfaceImpl();
        ReflectionTestUtils.setField(stpInterface, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(stpInterface, "objectMapper", new ObjectMapper());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getRoleListShouldReturnNullWhenNoData() {
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(1L))).thenReturn("");

        List<String> roles = stpInterface.getRoleList(1L, "login");

        assertNull(roles);
    }

    @Test
    void getRoleListShouldParseRoleJson() {
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(2L))).thenReturn("[\"ROOT\",\"ADMIN\"]");

        List<String> roles = stpInterface.getRoleList(2L, "login");

        assertEquals(List.of("ROOT", "ADMIN"), roles);
    }

    @Test
    void getPermissionListShouldReturnNullWhenRoleDataMissing() {
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(3L))).thenReturn(null);

        List<String> permissions = stpInterface.getPermissionList(3L, "login");

        assertNull(permissions);
    }

    @Test
    void getPermissionListShouldAggregatePermissionsFromAllRoles() {
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(4L))).thenReturn("[\"ROOT\",\"EDITOR\"]");
        when(valueOperations.multiGet(Arrays.asList(
                RedisKeyConstants.buildRolePermissionsKey("ROOT"),
                RedisKeyConstants.buildRolePermissionsKey("EDITOR")
        ))).thenReturn(Arrays.asList("[\"article:write\",\"article:read\"]", "[\"comment:read\"]"));

        List<String> permissions = stpInterface.getPermissionList(4L, "login");

        assertEquals(List.of("article:write", "article:read", "comment:read"), permissions);
    }

    @Test
    void getPermissionListShouldIgnoreInvalidPermissionJsonItems() {
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(5L))).thenReturn("[\"ROOT\",\"EDITOR\"]");
        when(valueOperations.multiGet(Arrays.asList(
                RedisKeyConstants.buildRolePermissionsKey("ROOT"),
                RedisKeyConstants.buildRolePermissionsKey("EDITOR")
        ))).thenReturn(Arrays.asList("not-json", "[\"valid:permission\"]"));

        List<String> permissions = stpInterface.getPermissionList(5L, "login");

        assertEquals(List.of("valid:permission"), permissions);
    }
}
