package com.xzf.blog.user.constant;

public interface MQConstants {

    /**
     * Topic 主题：延迟双删 Redis 用户缓存
     */
    String TOPIC_DELAY_DELETE_USER_REDIS_CACHE = "DelayDeleteUserRedisCacheTopic";

    /**
     * Topic 主题：用户删除事件
     */
    String USER_DELETE = "user_delete";

}
