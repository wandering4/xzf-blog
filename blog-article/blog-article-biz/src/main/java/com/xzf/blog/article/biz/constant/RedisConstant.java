package com.xzf.blog.article.biz.constant;


public class RedisConstant {

    private static final String ARTICLE_DETAIL = "article_detail";
    private static final String ARTICLE_PAGE_LIST = "article_page_list";
    private static final String ARTICLE_PAGE_BATCH_KEYS = "article_page_batch_keys"; // 存储分页缓存 key 集合

    /**
     * 文章详情缓存 Key
     */
    public static String buildArticleDetailKey(Long articleId) {
        return ARTICLE_DETAIL + ":" + articleId;
    }

    /**
     * 生成分页列表缓存 Key
     * 格式: article_page_list:{hash}
     * 通过将请求对象序列化后的 MD5 哈希值生成唯一 key
     */
    public static String buildArticlePageListKey(String reqJson) {
        // 使用 JDK 自带的 hashCode 生成摘要（正数）
        int hash = reqJson.hashCode();
        return ARTICLE_PAGE_LIST + ":" + Math.abs(hash);
    }

    /**
     * 获取存储分页缓存 key 集合的 Key
     * 格式: article_page_batch_keys:all (存储所有分页缓存 key)
     */
    public static String buildArticlePageBatchKeysKey(String batchType) {
        return ARTICLE_PAGE_BATCH_KEYS + ":" + batchType;
    }

    /**
     * 获取全部分页缓存集合的 Key
     */
    public static String buildArticlePageBatchKeysKeyForAll() {
        return buildArticlePageBatchKeysKey("all");
    }


}
