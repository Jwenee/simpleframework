package com.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.annotation.Controller;

import java.lang.reflect.Method;

@Slf4j
@Aspect(pointcut = "within(com.example.controller.superadmin.*)")
@Order(10)
public class ControllerInfoRecordAspect extends DefaultAspect {

    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("执行的类是[{}]，执行的方法是[{}]，参数是[{}]",
                targetClass.getName(), method.getName(), args);
    }

    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object retValue) throws Throwable {
        log.info("执行的类是[{}]，执行的方法是[{}]，参数是[{}]，返回值[{}]",
                targetClass.getName(), method.getName(), args, retValue);
        return retValue;
    }

    @Override
    public void afterThrowing(Class<?> targetClass, Method method, Object[] args, Throwable e) throws Throwable {
        log.info("执行的类是[{}]，执行的方法是[{}]，参数是[{}]，异常是[{}]",
                targetClass.getName(), method.getName(), args, e);
    }
}
