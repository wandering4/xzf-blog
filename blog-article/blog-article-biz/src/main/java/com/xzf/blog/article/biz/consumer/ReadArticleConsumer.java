package com.xzf.blog.article.biz.consumer;

import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.constants.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "article_group_" + MQConstants.TOPIC_READ_ARTICLE, // Group 组
        topic = MQConstants.TOPIC_READ_ARTICLE // 主题 Topic
)
public class ReadArticleConsumer implements RocketMQListener<Long> {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void onMessage(Long articleId) {
        //TODO:消息聚合

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，articleId: {}", articleId);

        // 执行文章阅读量 +1
        articleMapper.increaseReadNum(articleId);
        log.info("==> 文章阅读量 +1 操作成功，articleId: {}", articleId);
    }
}
