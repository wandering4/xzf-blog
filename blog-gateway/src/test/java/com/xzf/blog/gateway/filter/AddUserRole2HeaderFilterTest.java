package com.xzf.blog.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddUserRole2HeaderFilterTest {

    private AddUserRole2HeaderFilter filter;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        filter = new AddUserRole2HeaderFilter();
        ReflectionTestUtils.setField(filter, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(filter, "objectMapper", new ObjectMapper());
    }

    @Test
    void shouldPassThroughWhenUserIdHeaderMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ROLE));
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void shouldPassThroughWhenUserIdHeaderBlank() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(GlobalConstants.USER_ID, " ").build()
        );
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ROLE));
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void shouldPassThroughWhenUserIdIsInvalidNumber() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(GlobalConstants.USER_ID, "abc").build()
        );
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ROLE));
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void shouldPassThroughWhenRoleValueMissingInRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(10L))).thenReturn("");

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(GlobalConstants.USER_ID, "10").build()
        );
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertNull(chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ROLE));
    }

    @Test
    void shouldAddUserRoleHeaderWhenRedisContainsRole() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisKeyConstants.buildUserRoleKey(11L))).thenReturn("ROOT");

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(GlobalConstants.USER_ID, "11").build()
        );
        CapturingChain chain = new CapturingChain();

        filter.filter(exchange, chain).block();

        assertEquals(1, chain.calledTimes);
        assertEquals("ROOT", chain.exchange.getRequest().getHeaders().getFirst(GlobalConstants.USER_ROLE));
    }

    @Test
    void orderShouldBeTwo() {
        assertEquals(2, filter.getOrder());
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
