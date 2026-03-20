package com.xzf.framework.biz.context.filter;

import com.xzf.blog.framework.commons.constant.GlobalConstants;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderUserRole2ContextFilterTest {

    private final HeaderUserRole2ContextFilter filter = new HeaderUserRole2ContextFilter();

    @AfterEach
    void tearDown() {
        LoginUserContextHolder.remove();
    }

    @Test
    void shouldSetUserRoleInChainAndClearAfterwards() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(GlobalConstants.USER_ROLE, "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> roleSeenInChain = new AtomicReference<>();

        FilterChain chain = (req, res) -> roleSeenInChain.set(LoginUserContextHolder.getUserRole());

        filter.doFilter(request, response, chain);

        assertEquals("ADMIN", roleSeenInChain.get());
        assertNull(LoginUserContextHolder.getUserRole());
    }

    @Test
    void shouldPassThroughWhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean called = new AtomicBoolean(false);

        FilterChain chain = (req, res) -> called.set(true);

        filter.doFilter(request, response, chain);

        assertTrue(called.get());
        assertNull(LoginUserContextHolder.getUserRole());
    }
}
