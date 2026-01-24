package com.xzf.framework.biz.context.aspect;

import com.google.common.collect.Lists;
import com.xzf.blog.framework.commons.enums.ResponseCodeEnum;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.util.JsonUtils;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.validation.BindException;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Slf4j
public class PreAuthorizeAspect {

    @Pointcut("@annotation(com.xzf.framework.biz.context.aspect.PreAuthorize)")
    public void preAuthorize() {}


    @Around("preAuthorize()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("PreAuthorizeAspect执行鉴权...");
        Long userId = LoginUserContextHolder.getUserId();
        if (userId == null) {
            throw new BizException(ResponseCodeEnum.NOT_LOGIN);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 2. 使用 MethodSignature 获取当前被注解的 Method
        Method method = signature.getMethod();

        // 3. 从 Method 中提取 LogExecution 注解
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        String[] needRoleList = preAuthorize.hasRoles();
        List<String> roleList = Lists.newArrayList(LoginUserContextHolder.getUserRole());
        log.info("鉴权所需角色:{},用户角色:{}", JsonUtils.toJsonString(needRoleList), JsonUtils.toJsonString(roleList));
        for (String role : needRoleList) {
            if (!roleList.contains(role)) {
                throw new BizException(ResponseCodeEnum.NOT_HAVE_PERMISSION);
            }
        }

        // 执行切点方法
        return joinPoint.proceed();
    }

}
