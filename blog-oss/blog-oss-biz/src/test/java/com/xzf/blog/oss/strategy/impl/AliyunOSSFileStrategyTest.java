package com.xzf.blog.oss.strategy.impl;

import com.aliyun.oss.OSS;
import com.xzf.blog.oss.config.AliyunOSSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AliyunOSSFileStrategyTest {

    @Mock
    private OSS ossClient;

    private AliyunOSSFileStrategy aliyunOSSFileStrategy;

    @BeforeEach
    void setUp() {
        aliyunOSSFileStrategy = new AliyunOSSFileStrategy();
        AliyunOSSProperties properties = new AliyunOSSProperties();
        properties.setEndpoint("oss-cn-test.aliyuncs.com");
        ReflectionTestUtils.setField(aliyunOSSFileStrategy, "aliyunOSSProperties", properties);
        ReflectionTestUtils.setField(aliyunOSSFileStrategy, "ossClient", ossClient);
    }

    @Test
    void shouldUploadToAliyunAndReturnGeneratedUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "xyz".getBytes(StandardCharsets.UTF_8)
        );
        when(ossClient.putObject(anyString(), anyString(), any(InputStream.class))).thenReturn(null);

        String url = aliyunOSSFileStrategy.uploadFile(file, "xzf-bucket");

        assertTrue(url.startsWith("https://xzf-bucket.oss-cn-test.aliyuncs.com/"));
        assertTrue(url.endsWith(".jpg"));

        ArgumentCaptor<String> objectNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(ossClient).putObject(anyString(), objectNameCaptor.capture(), any(InputStream.class));
        assertTrue(objectNameCaptor.getValue().endsWith(".jpg"));
    }

    @Test
    void shouldThrowWhenFileIsNull() {
        assertThrows(RuntimeException.class, () -> aliyunOSSFileStrategy.uploadFile(null, "xzf-bucket"));
    }

    @Test
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThrows(RuntimeException.class, () -> aliyunOSSFileStrategy.uploadFile(file, "xzf-bucket"));
    }

    @Test
    void shouldThrowWhenFileHasNoExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "filename",
                "image/jpeg",
                "xyz".getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(IllegalArgumentException.class, () -> aliyunOSSFileStrategy.uploadFile(file, "xzf-bucket"));
    }
}
