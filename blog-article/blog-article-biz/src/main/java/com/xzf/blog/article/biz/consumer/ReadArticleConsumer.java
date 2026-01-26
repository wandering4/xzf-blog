package com.xzf.blog.article.biz.consumer;

import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.constants.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "article_group_" + MQConstants.TOPIC_READ_ARTICLE,
        topic = MQConstants.TOPIC_READ_ARTICLE,
        consumeMode = ConsumeMode.CONCURRENTLY,  // 并发消费提高吞吐量
        consumeThreadNumber = 8,  // 增加消费线程数
        maxReconsumeTimes = 3  // 最大重试次数
)
public class ReadArticleConsumer implements RocketMQListener<Long> {

    @Autowired
    private ArticleMapper articleMapper;

    // 聚合缓存：文章ID -> 阅读次数
    private final Map<Long, AtomicInteger> readCountMap = new ConcurrentHashMap<>();

    // 定时任务执行器
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isFlushing = new AtomicBoolean(false);

    // 批量大小阈值
    private static final int BATCH_SIZE_THRESHOLD = 100;

    @PostConstruct
    public void init() {
        // 启动定时任务，每5秒刷新一次到数据库
        scheduler.scheduleAtFixedRate(() -> {
            flushToDatabase();
        }, 5, 5, TimeUnit.SECONDS);

        // 启动定时日志输出
        scheduler.scheduleAtFixedRate(this::logAggregationStatus, 30, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if (scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                // 优雅关闭前，刷新剩余数据
                flushToDatabase();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onMessage(Long articleId) {
        String threadName = Thread.currentThread().getName();
        log.info("==> threadName: {}, 收到文章阅读消息，articleId: {}", threadName, articleId);

        // 1. 聚合阅读次数
        aggregateReadCount(articleId);

        // 2. 检查是否需要批量刷新
        if (shouldFlushImmediately()) {
            flushToDatabase();
        }
    }

    /**
     * 聚合阅读次数
     */
    private void aggregateReadCount(Long articleId) {
        readCountMap.computeIfAbsent(articleId, id -> new AtomicInteger(0))
                .incrementAndGet();

        // 监控聚合大小
        int currentSize = readCountMap.size();
        if (currentSize % 20 == 0) {  // 每20个不同文章记录一次日志
            log.info("==> 聚合缓存当前大小: {} 个不同文章", currentSize);
        }
    }

    /**
     * 判断是否需要立即刷新
     */
    private boolean shouldFlushImmediately() {
        int totalReads = readCountMap.values().stream()
                .mapToInt(AtomicInteger::get)
                .sum();

        // 条件1: 总阅读次数超过阈值
        if (totalReads >= BATCH_SIZE_THRESHOLD) {
            return true;
        }

        // 条件2: 不同文章数量超过阈值
        if (readCountMap.size() >= 50) {
            return true;
        }

        return false;
    }

    /**
     * 刷新到数据库
     */
    private void flushToDatabase() {
        if (readCountMap.isEmpty() || !isFlushing.compareAndSet(false, true)) {
            return;
        }

        try {
            long startTime = System.currentTimeMillis();

            // 复制当前数据，避免长时间锁住map
            Map<Long, Integer> batchData = new HashMap<>();
            readCountMap.forEach((articleId, counter) -> {
                int count = counter.get();
                if (count > 0) {
                    batchData.put(articleId, count);
                }
            });

            if (batchData.isEmpty()) {
                return;
            }

            log.info("==> 开始批量更新数据库，涉及文章数: {}", batchData.size());

            int successCount = 0;
            int totalIncrement = 0;

            // 批量更新数据库
            for (Map.Entry<Long, Integer> entry : batchData.entrySet()) {
                Long articleId = entry.getKey();
                Integer count = entry.getValue();

                try {
                    // 使用批量更新方法
                    int rows = articleMapper.increaseViewCount(articleId, count);
                    if (rows > 0) {
                        successCount++;
                        totalIncrement += count;

                        // 更新成功后，从缓存中减去已处理的数量
                        AtomicInteger remaining = readCountMap.get(articleId);
                        if (remaining != null) {
                            remaining.addAndGet(-count);
                            if (remaining.get() <= 0) {
                                readCountMap.remove(articleId);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("==> 更新文章阅读量失败，articleId: {}, count: {}", articleId, count, e);
                }
            }

            long costTime = System.currentTimeMillis() - startTime;
            log.info("==> 批量更新完成，成功: {} 篇，总增加阅读量: {}，耗时: {}ms",
                    successCount, totalIncrement, costTime);

        } finally {
            isFlushing.set(false);
        }
    }

    /**
     * 记录聚合状态日志
     */
    private void logAggregationStatus() {
        int articleCount = readCountMap.size();
        int totalReads = readCountMap.values().stream()
                .mapToInt(AtomicInteger::get)
                .sum();

        log.info("==> 聚合状态统计 - 文章数: {}, 待更新阅读量: {}, 内存占用: 约{}KB",
                articleCount,
                totalReads,
                estimateMemoryUsage());
    }

    /**
     * 估算内存占用
     */
    private int estimateMemoryUsage() {
        // 简化估算：每个Long+AtomicInteger约32字节
        return readCountMap.size() * 32 / 1024;
    }

    /**
     * 手动刷新接口（可用于监控或管理）
     */
    public Map<String, Object> manualFlush() {
        long startTime = System.currentTimeMillis();
        int beforeSize = readCountMap.size();

        flushToDatabase();

        int afterSize = readCountMap.size();
        long costTime = System.currentTimeMillis() - startTime;

        Map<String, Object> result = new HashMap<>();
        result.put("beforeSize", beforeSize);
        result.put("afterSize", afterSize);
        result.put("costTime", costTime);
        result.put("timestamp", new Date());

        return result;
    }

    /**
     * 获取聚合状态（用于监控）
     */
    public Map<String, Object> getAggregationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("articleCount", readCountMap.size());
        status.put("totalReads", readCountMap.values().stream()
                .mapToInt(AtomicInteger::get).sum());
        status.put("topArticles", getTopArticles(10));
        status.put("isFlushing", isFlushing.get());

        return status;
    }

    /**
     * 获取阅读量最高的文章
     */
    private List<Map<String, Object>> getTopArticles(int limit) {
        return readCountMap.entrySet().stream()
                .filter(entry -> entry.getValue().get() > 0)
                .sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("articleId", entry.getKey());
                    item.put("readCount", entry.getValue().get());
                    return item;
                })
                .collect(Collectors.toList());
    }
}