package com.xzf.blog.comment.biz.consumer;

import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserConsumerTest {

    private DeleteUserConsumer consumer;

    @Mock
    private CommentDOMapper commentDOMapper;

    @BeforeEach
    void setUp() {
        consumer = new DeleteUserConsumer();
        ReflectionTestUtils.setField(consumer, "commentDOMapper", commentDOMapper);
    }

    @Test
    void onMessageShouldDeleteCommentsByUserId() {
        consumer.onMessage(5L);

        verify(commentDOMapper).deleteByUserId(5L);
    }
}
