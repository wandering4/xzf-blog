package com.xzf.blog.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzf.blog.framework.commons.constant.GlobalConstants;
import com.xzf.blog.framework.commons.constant.RedisKeyConstants;
import com.xzf.blog.framework.commons.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AddUserRole2HeaderFilter implements GlobalFilter, Ordered {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Header 头中 Token 的 Key
     */
    private static final String TOKEN_HEADER_KEY = "Authorization";

    /**
     * Token 前缀
     */
    private static final String TOKEN_HEADER_VALUE_PREFIX = "Bearer ";

    @Resource
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("==================> UserRoleConvertFilter");

        // 从请求头中获取 userId（由 AddUserId2HeaderFilter 设置）
        List<String> userIdList = exchange.getRequest().getHeaders().get(GlobalConstants.USER_ID);

        if (CollUtil.isEmpty(userIdList)) {
            // 若请求头中未携带 userId，则直接放行
            log.info("## 请求头中未携带 userId，直接放行");
            return chain.filter(exchange);
        }

        // 获取 userId 值并转换为 Long
        String userIdStr = userIdList.get(0);
        if (StringUtils.isBlank(userIdStr)) {
            log.info("## userId 为空，直接放行");
            return chain.filter(exchange);
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.warn("## userId 格式错误: {}", userIdStr);
            return chain.filter(exchange);
        }

        log.info("## 当前登录的用户 ID: {}", userId);

        // 构建 用户-角色 Redis Key
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);

        // 根据用户 ID ，从 Redis 中获取该用户的角色集合
        String userRole = redisTemplate.opsForValue().get(userRolesKey);

        if (StringUtils.isBlank(userRole)) {
            log.info("## 用户角色数据为空，直接放行");
            return chain.filter(exchange);
        }

        log.info("## 当前登录的用户角色: {}", userRole);

        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.header(GlobalConstants.USER_ROLE, userRole)) // 将用户角色设置到请求头中
                .build();
        return chain.filter(newExchange);
    }

    /**
     * 设置过滤器执行顺序，数字越小优先级越高（先执行）
     * 此过滤器后执行，依赖 AddUserId2HeaderFilter 设置的 userId
     */
    @Override
    public int getOrder() {
        return 2;
    }
}