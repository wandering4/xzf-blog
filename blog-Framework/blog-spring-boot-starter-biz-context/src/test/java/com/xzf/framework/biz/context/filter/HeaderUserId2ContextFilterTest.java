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

class HeaderUserId2ContextFilterTest {

    private final HeaderUserId2ContextFilter filter = new HeaderUserId2ContextFilter();

    @AfterEach
    void tearDown() {
        LoginUserContextHolder.remove();
    }

    @Test
    void shouldSetUserIdInChainAndClearAfterwards() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(GlobalConstants.USER_ID, "123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Long> userIdSeenInChain = new AtomicReference<>();

        FilterChain chain = (req, res) -> userIdSeenInChain.set(LoginUserContextHolder.getUserId());

        filter.doFilter(request, response, chain);

        assertEquals(123L, userIdSeenInChain.get());
        assertNull(LoginUserContextHolder.getUserId());
    }

    @Test
    void shouldPassThroughWhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean called = new AtomicBoolean(false);

        FilterChain chain = (req, res) -> called.set(true);

        filter.doFilter(request, response, chain);

        assertTrue(called.get());
        assertNull(LoginUserContextHolder.getUserId());
    }
}
