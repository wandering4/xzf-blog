package com.xzf.framework.biz.context.config;


import com.xzf.framework.biz.context.filter.HeaderUserId2ContextFilter;
import com.xzf.framework.biz.context.filter.HeaderUserRole2ContextFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
public class ContextAutoConfiguration {

    @Bean
    public FilterRegistrationBean<HeaderUserId2ContextFilter> filterFilterRegistrationIdBean() {
        HeaderUserId2ContextFilter filter = new HeaderUserId2ContextFilter();
        FilterRegistrationBean<HeaderUserId2ContextFilter> bean = new FilterRegistrationBean<>(filter);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<HeaderUserRole2ContextFilter> filterFilterRegistrationRoleBean() {
        HeaderUserRole2ContextFilter filter = new HeaderUserRole2ContextFilter();
        FilterRegistrationBean<HeaderUserRole2ContextFilter> bean = new FilterRegistrationBean<>(filter);
        return bean;
    }
}
