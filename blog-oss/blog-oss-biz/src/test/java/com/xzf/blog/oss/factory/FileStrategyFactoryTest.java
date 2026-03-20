package com.xzf.blog.oss.factory;

import com.xzf.blog.oss.strategy.FileStrategy;
import com.xzf.blog.oss.strategy.impl.AliyunOSSFileStrategy;
import com.xzf.blog.oss.strategy.impl.MinioFileStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStrategyFactoryTest {

    @Test
    void shouldReturnMinioStrategy() {
        FileStrategyFactory factory = new FileStrategyFactory();
        ReflectionTestUtils.setField(factory, "strategyType", "minio");

        FileStrategy strategy = factory.getFileStrategy();

        assertInstanceOf(MinioFileStrategy.class, strategy);
    }

    @Test
    void shouldReturnAliyunStrategy() {
        FileStrategyFactory factory = new FileStrategyFactory();
        ReflectionTestUtils.setField(factory, "strategyType", "aliyun");

        FileStrategy strategy = factory.getFileStrategy();

        assertInstanceOf(AliyunOSSFileStrategy.class, strategy);
    }

    @Test
    void shouldThrowWhenStrategyTypeIsUnsupported() {
        FileStrategyFactory factory = new FileStrategyFactory();
        ReflectionTestUtils.setField(factory, "strategyType", "s3");

        assertThrows(IllegalArgumentException.class, factory::getFileStrategy);
    }
}
