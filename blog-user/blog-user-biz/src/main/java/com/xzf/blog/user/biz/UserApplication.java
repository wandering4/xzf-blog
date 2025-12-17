package com.xzf.blog.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xzf.blog.user.biz.domain.mapper")
@SpringBootApplication
public class UserApplication {
    public static void main( String[] args ) {
        SpringApplication.run(UserApplication.class, args);
    }
}
