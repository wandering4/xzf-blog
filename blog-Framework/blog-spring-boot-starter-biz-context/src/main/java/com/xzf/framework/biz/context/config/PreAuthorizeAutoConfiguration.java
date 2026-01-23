package com.xzf.framework.biz.context.config;

import com.xzf.framework.biz.context.aspect.PreAuthorizeAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class PreAuthorizeAutoConfiguration {
    @Bean
    public PreAuthorizeAspect preAuthorizeAspect() {
        return new PreAuthorizeAspect();
    }
}
