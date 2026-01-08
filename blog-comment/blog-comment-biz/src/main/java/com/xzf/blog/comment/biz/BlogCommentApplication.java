package com.xzf.blog.comment.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients(basePackages = "com.xzf.blog")
@MapperScan("com.xzf.blog.comment.biz.domain.mapper")
@SpringBootApplication
public class BlogCommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogCommentApplication.class, args);
    }
}
