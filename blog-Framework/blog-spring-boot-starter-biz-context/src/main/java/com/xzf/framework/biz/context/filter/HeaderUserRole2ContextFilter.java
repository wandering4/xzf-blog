package com.xzf.framework.biz.context.filter;



import com.xzf.blog.framework.commons.constant.GlobalConstants;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet
        .FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**

 * @description: 提取请求头中的用户 角色 保存到上下文中，以方便后续使用
 **/
@Slf4j
public class HeaderUserRole2ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取用户角色
        String userRole = request.getHeader(GlobalConstants.USER_ROLE);

        // 判断请求头中是否存在用户角色
        if (StringUtils.isBlank(userRole)) {
            // 若为空，则直接放行
            chain.doFilter(request, response);
            return;
        }

        log.info("===== 设置 userRole 到 ThreadLocal 中， 用户 角色: {}", userRole);
        LoginUserContextHolder.setUserRole(userRole);

        try {
            chain.doFilter(request, response);
        } finally {
            // 一定要删除 ThreadLocal ，防止内存泄露
            LoginUserContextHolder.remove();
            log.info("===== 删除 ThreadLocal， userRole: {}", userRole);
        }
    }
}
