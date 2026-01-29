package com.xzf.blog.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于IP的分布式限流过滤器
 * 对于同一IP短时间对不同项目的多次访问进行限流
 */
@Slf4j
@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    @Value("${application.qps:10}")
    private int defaultQps;

    /**
     * 不同项目的QPS配置，key为项目路径前缀，value为QPS限制
     */
    @Value("${application.rate-limit.user-qps:20}")
    private int userQps;

    @Value("${application.rate-limit.comment-qps:30}")
    private int commentQps;

    @Value("${application.rate-limit.article-qps:50}")
    private int articleQps;

    @Value("${application.rate-limit.ai-qps:10}")
    private int aiQps;

    @Value("${application.rate-limit.file-qps:15}")
    private int fileQps;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Lua脚本：实现滑动窗口限流
     * 返回值：1表示允许，0表示被限流
     */
    private static final String RATE_LIMIT_SCRIPT = """
        -- 限流滑动窗口算法
        -- KEYS[1]: 限流的key
        -- ARGV[1]: 限流阈值
        -- ARGV[2]: 时间窗口大小（毫秒）
        -- ARGV[3]: 过期时间（秒）
        
        local key = KEYS[1]
        local limit = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local expire = tonumber(ARGV[3])
        
        if limit == nil or window == nil or expire == nil then
            return 1
        end
        
        -- 获取当前时间戳（毫秒）
        local now = redis.call('time')[1] * 1000 + math.floor(redis.call('time')[2] / 1000)
        
        -- 计算窗口开始时间
        local windowStart = now - window
        
        -- 移除窗口外的数据
        redis.call('zremrangebyscore', key, 0, windowStart)
        
        -- 获取当前窗口内的请求数量
        local current = redis.call('zcard', key)
        
        if current < limit then
            -- 添加当前请求
            local member = tostring(now) .. '-' .. tostring(math.random(10000, 99999))
            redis.call('zadd', key, now, member)
            -- 设置过期时间
            redis.call('expire', key, expire)
            return 1
        end
        
        return 0
        """;

    private final RedisScript<Boolean> rateLimitScript = RedisScript.of(RATE_LIMIT_SCRIPT, Boolean.class);

    /**
     * 项目路径前缀与QPS配置的映射
     */
    private Map<String, Integer> getProjectQpsMap() {
        Map<String, Integer> qpsMap = new HashMap<>();
        qpsMap.put("user", userQps);
        qpsMap.put("comment", commentQps);
        qpsMap.put("article", articleQps);
        qpsMap.put("ai", aiQps);
        qpsMap.put("file", fileQps);
        return qpsMap;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String clientIp = getClientIp(exchange);
        String project = getProjectFromPath(path);
        int qpsLimit = getQpsLimit(project);

        // 本地IP跳过限流
//        if (isLocalIp(clientIp)) {
//            return chain.filter(exchange);
//        }

        // 构建Redis key: rate_limit:ip:project
        String redisKey = String.format("rate_limit:%s:%s", clientIp, project);
        // 时间窗口：1秒（毫秒）
        long window = 1000L;
        // 过期时间：2秒
        long expire = 2L;

        try {
            Boolean allowed = redisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(redisKey),
                    qpsLimit,  // 直接传递数字
                    window,    // 直接传递数字
                    expire     // 直接传递数字
            );

            log.info("限流检查: IP={}, 项目={}, 路径={}, 结果={}, 限制={}次/秒",
                    clientIp, project, path, allowed, qpsLimit);

            if (allowed != null && !allowed) {
                log.warn("IP: {} 访问项目: {} 触发限流, 路径: {}, 限制: {}次/秒",
                        clientIp, project, path, qpsLimit);
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                String errorMsg = String.format("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试。限制：%d次/秒\"}", qpsLimit);
                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(errorMsg.getBytes()))
                );
            }
        } catch (Exception e) {
            log.error("限流检查异常, IP: {}, 项目: {}, 路径: {}, 错误: {}",
                    clientIp, project, path, e.getMessage(), e);
            // 限流异常时放行，避免影响正常业务
        }
        return chain.filter(exchange);
    }

    /**
     * 检查是否为本地IP
     */
    private boolean isLocalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return ip.equals("0:0:0:0:0:0:0:1") ||
                ip.equals("127.0.0.1") ||
                ip.equals("localhost") ||
                ip.equals("::1");
    }

    /**
     * 从请求路径中提取项目名称
     */
    private String getProjectFromPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "default";
        }

        // 确保路径以/开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 分割路径
        String[] parts = path.split("/");
        if (parts.length >= 2 && !parts[1].isEmpty()) {
            return parts[1];
        }
        return "default";
    }

    /**
     * 获取项目的QPS限制
     */
    private int getQpsLimit(String project) {
        Map<String, Integer> qpsMap = getProjectQpsMap();
        return qpsMap.getOrDefault(project, defaultQps);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerWebExchange exchange) {
        // 优先从X-Forwarded-For头获取真实IP
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            ip = ip.split(",")[0].trim();
            return ip;
        }

        // 检查代理IP头
        ip = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 检查Proxy-Client-IP
        ip = exchange.getRequest().getHeaders().getFirst("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 检查WL-Proxy-Client-IP
        ip = exchange.getRequest().getHeaders().getFirst("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 最后从RemoteAddress获取
        try {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}