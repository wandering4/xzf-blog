package com.xzf.blog.comment.biz.consumer;

import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import com.xzf.blog.user.constant.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "comment_group_" + MQConstants.USER_DELETE,
        topic = MQConstants.USER_DELETE
)
public class DeleteUserConsumer implements RocketMQListener<Long> {

    @Autowired
    private CommentDOMapper commentDOMapper;

    @Override
    public void onMessage(Long userId) {
        log.info("DeleteUserConsumer消费消息:{}",userId);
        long delete=commentDOMapper.deleteByUserId(userId);
        log.info("DeleteUserConsumer消费成功，删除{}条评论数据",delete);
    }

}
