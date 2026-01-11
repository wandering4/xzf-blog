package com.xzf.blog.comment.biz.consumer;

import com.xzf.blog.article.constants.MQConstants;
import com.xzf.blog.comment.biz.domain.mapper.CommentDOMapper;
import com.xzf.blog.framework.commons.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "comment_group_" + MQConstants.TOPIC_DELETE_ARTICLE, // Group 组
        topic = MQConstants.TOPIC_DELETE_ARTICLE // 主题 Topic
)
public class ArticleDeleteConsumer implements RocketMQListener<String> {

    @Autowired
    private CommentDOMapper commentDOMapper;

    @Override
    public void onMessage(String body) {
        log.info("消费到文章删除事件:{}", body);
        Long articleId = JsonUtils.parseObject(body, Long.class);
        if (articleId == null) {
            log.error("文章删除事件序列化失败:{}", body);
            return;
        }
        int delete = commentDOMapper.deleteByArticleId(articleId);
        log.info("文章删除事件消费成功，删除{}条", delete);
    }
}
