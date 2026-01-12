package com.xzf.blog.article.constants;

public interface MQConstants {

    /**
     * 文章发布事件
     */
    String TOPIC_PUBLISH_ARTICLE = "topic_publish_article";

    /**
     * 文章更新事件
     */
    String TOPIC_UPDATE_ARTICLE = "topic_update_article";

    /**
     * 文章删除事件
     */
    String TOPIC_DELETE_ARTICLE = "topic_delete_article";

    /**
     * 文章阅读事件
     */
    String TOPIC_READ_ARTICLE = "topic_read_article";


}
