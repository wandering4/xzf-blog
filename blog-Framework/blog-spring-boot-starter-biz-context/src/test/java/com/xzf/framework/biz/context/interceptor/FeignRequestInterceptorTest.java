package com.xzf.framework.biz.context.interceptor;

import com.xzf.blog.framework.commons.constant.GlobalConstants;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FeignRequestInterceptorTest {

    private final FeignRequestInterceptor interceptor = new FeignRequestInterceptor();

    @AfterEach
    void tearDown() {
        LoginUserContextHolder.remove();
    }

    @Test
    void shouldAddUserHeadersWhenContextExists() {
        LoginUserContextHolder.setUserId(1001L);
        LoginUserContextHolder.setUserRole("ROOT");
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals("1001", template.headers().get(GlobalConstants.USER_ID).iterator().next());
        assertEquals("ROOT", template.headers().get(GlobalConstants.USER_ROLE).iterator().next());
    }

    @Test
    void shouldNotAddHeadersWhenContextIsEmpty() {
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey(GlobalConstants.USER_ID));
        assertFalse(template.headers().containsKey(GlobalConstants.USER_ROLE));
    }
}
