package com.xzf.blog.article.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.xzf.blog.article.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.xzf.blog")
public class BlogArticleApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogArticleApplication.class, args);
    }
}
