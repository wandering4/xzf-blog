package com.xzf.blog.gateway.filter;

import com.xzf.blog.framework.commons.constant.GlobalConstants;
import com.xzf.blog.framework.commons.constant.RedisKeyConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddUserId2HeaderFilterTest {

    private AddUserId2HeaderFilter filter;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        filter = new AddUserId2HeaderFilter();
        ReflectionTestUtils.setField(filter, "redisTemplate", redisTemplate);
    }

    @Test
    void shouldPassThroughWhenAuthorizationHeaderMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ID));
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void shouldPassThroughWhenTokenNotFoundInRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisKeyConstants.SA_TOKEN_TOKEN_KEY_PREFIX + "token-1")).thenReturn(null);

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("Authorization", "Bearer token-1")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ID));
    }

    @Test
    void shouldAddUserIdHeaderWhenTokenResolvedFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisKeyConstants.SA_TOKEN_TOKEN_KEY_PREFIX + "token-2")).thenReturn(99);

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("Authorization", "Bearer token-2")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertEquals("99", chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ID));
    }

    @Test
    void orderShouldBeOne() {
        assertTrue(filter.getOrder() == 1);
    }

    private static final class CapturingChain implements GatewayFilterChain {
        private ServerWebExchange exchange;
        private int calledTimes;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange) {
            this.exchange = exchange;
            this.calledTimes++;
            return Mono.empty();
        }
    }
}
