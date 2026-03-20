package com.xzf.framework.biz.context.holder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginUserContextHolderTest {

    @AfterEach
    void tearDown() {
        LoginUserContextHolder.remove();
    }

    @Test
    void shouldSetAndGetUserIdAndRole() {
        LoginUserContextHolder.setUserId("123");
        LoginUserContextHolder.setUserRole("ROOT");

        assertEquals(123L, LoginUserContextHolder.getUserId());
        assertEquals("ROOT", LoginUserContextHolder.getUserRole());
    }

    @Test
    void removeShouldClearThreadLocalValues() {
        LoginUserContextHolder.setUserId(1L);
        LoginUserContextHolder.setUserRole("ADMIN");

        LoginUserContextHolder.remove();

        assertNull(LoginUserContextHolder.getUserId());
        assertNull(LoginUserContextHolder.getUserRole());
    }
}
