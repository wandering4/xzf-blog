package com.xzf.blog.comment.biz.consumer;

import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleDeleteConsumerTest {

    private ArticleDeleteConsumer consumer;

    @Mock
    private CommentDOMapper commentDOMapper;

    @BeforeEach
    void setUp() {
        consumer = new ArticleDeleteConsumer();
        ReflectionTestUtils.setField(consumer, "commentDOMapper", commentDOMapper);
    }

    @Test
    void onMessageShouldIgnoreInvalidPayload() {
        assertThatCode(() -> consumer.onMessage("null")).doesNotThrowAnyException();

        verify(commentDOMapper, never()).deleteByArticleId(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void onMessageShouldDeleteCommentsByArticleId() {
        consumer.onMessage("12");

        verify(commentDOMapper).deleteByArticleId(12L);
    }
}
