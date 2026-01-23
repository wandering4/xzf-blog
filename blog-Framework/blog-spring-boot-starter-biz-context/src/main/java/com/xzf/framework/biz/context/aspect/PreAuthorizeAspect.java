package com.xzf.framework.biz.context.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class PreAuthorizeAspect {

    @Pointcut("@annotation(com.xzf.framework.biz.context.aspect.PreAuthorize)")
    public void preAuthorize() {}


    @Around("preAuthorize()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //TODO
        // 执行切点方法
        Object result = joinPoint.proceed();
        return result;
    }

}
