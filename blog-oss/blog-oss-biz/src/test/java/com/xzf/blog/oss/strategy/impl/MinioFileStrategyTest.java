package com.xzf.blog.oss.strategy.impl;

import com.xzf.blog.oss.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioFileStrategyTest {

    @Mock
    private MinioClient minioClient;

    private MinioFileStrategy minioFileStrategy;

    @BeforeEach
    void setUp() {
        minioFileStrategy = new MinioFileStrategy();
        MinioProperties properties = new MinioProperties();
        properties.setEndpoint("http://minio.local");
        ReflectionTestUtils.setField(minioFileStrategy, "minioProperties", properties);
        ReflectionTestUtils.setField(minioFileStrategy, "minioClient", minioClient);
    }

    @Test
    void shouldUploadToMinioAndReturnGeneratedUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                "abc".getBytes(StandardCharsets.UTF_8)
        );
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String url = minioFileStrategy.uploadFile(file, "xzf-bucket");

        assertTrue(url.startsWith("http://minio.local/xzf-bucket/"));
        assertTrue(url.endsWith(".png"));

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
        assertTrue(captor.getValue().bucket().equals("xzf-bucket"));
        assertTrue(captor.getValue().object().endsWith(".png"));
    }

    @Test
    void shouldThrowWhenFileIsNull() {
        assertThrows(RuntimeException.class, () -> minioFileStrategy.uploadFile(null, "xzf-bucket"));
    }

    @Test
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThrows(RuntimeException.class, () -> minioFileStrategy.uploadFile(file, "xzf-bucket"));
    }

    @Test
    void shouldThrowWhenFileHasNoExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "filename", "image/png", "abc".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> minioFileStrategy.uploadFile(file, "xzf-bucket"));
    }
}
