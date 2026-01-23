package com.xzf.framework.biz.context.aspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PreAuthorize {
    String[] hasRoles() default {};
}
