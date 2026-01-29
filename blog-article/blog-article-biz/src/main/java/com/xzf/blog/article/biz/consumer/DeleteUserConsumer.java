package com.xzf.blog.article.biz.consumer;

import com.xzf.blog.article.biz.domain.dataobject.ArticleDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleCategoryMapper;
import com.xzf.blog.article.biz.domain.mapper.ArticleContentMapper;
import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.biz.domain.mapper.ArticleTagMapper;
import com.xzf.blog.user.constant.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "article_group_" + MQConstants.USER_DELETE,
        topic = MQConstants.USER_DELETE
)
public class DeleteUserConsumer implements RocketMQListener<Long> {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(Long userId) {
        log.info("DeleteUserConsumer消费消息:{}", userId);
        List<Long> articleIds = articleMapper.selectByUserId(userId).stream().map(ArticleDO::getId).toList();
        int deleteArticle = articleMapper.deleteBatchIds(articleIds);
        int deleteArticleContent = articleContentMapper.deleteByArticleIds(articleIds);
        int deleteCategoryContent = articleCategoryMapper.deleteByArticleIds(articleIds);
        int deleteArticleTag = articleTagMapper.deleteByArticleIds(articleIds);
        log.info("DeleteUserConsumer消费成功，删除{}条文章，{}条文章内容，{}条文章分类关联关系，{}条文章标签关联关系", deleteArticle, deleteArticleContent, deleteCategoryContent, deleteArticleTag);

        for (Long articleId : articleIds) {
            Message<Long> message = MessageBuilder.withPayload(articleId).build();
            rocketMQTemplate.asyncSend(com.xzf.blog.article.constants.MQConstants.TOPIC_DELETE_ARTICLE, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("==> 【文章服务：删除文章】MQ 发送成功，SendResult: {}", sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("==> 【文章服务：删除文章】MQ 发送异常: ", throwable);
                }
            });
        }
    }

}
