package com.xzf.blog.user.biz.consumer;

import com.xzf.blog.user.biz.constant.RedisKeyConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DelayDeleteUserRedisCacheConsumerTest {

    private DelayDeleteUserRedisCacheConsumer consumer;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        consumer = new DelayDeleteUserRedisCacheConsumer();
        ReflectionTestUtils.setField(consumer, "redisTemplate", redisTemplate);
    }

    @Test
    void onMessageShouldDeleteUserCaches() {
        consumer.onMessage("7");

        verify(redisTemplate).delete(List.of(
                RedisKeyConstants.buildUserInfoKey(7L),
                RedisKeyConstants.buildUserProfileKey(7L)
        ));
    }
}
