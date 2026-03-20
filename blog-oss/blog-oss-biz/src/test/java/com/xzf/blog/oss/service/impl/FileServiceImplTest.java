package com.xzf.blog.oss.service.impl;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.oss.strategy.FileStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileStrategy fileStrategy;
    @Mock
    private MultipartFile multipartFile;

    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl();
        ReflectionTestUtils.setField(fileService, "fileStrategy", fileStrategy);
    }

    @Test
    void shouldUploadFileUsingDefaultBucketName() {
        when(fileStrategy.uploadFile(multipartFile, "xzf-blog")).thenReturn("https://cdn/a.png");

        Response<String> response = fileService.uploadFile(multipartFile);

        assertTrue(response.isSuccess());
        assertEquals("https://cdn/a.png", response.getData());
        verify(fileStrategy).uploadFile(multipartFile, "xzf-blog");
    }
}
