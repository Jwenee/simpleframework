package org.simpleframework.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyCreator {

    // 创建动态代理对象并返回
    public static Object createProxy(Class<?> targetClass, MethodInterceptor methodInterceptor) {
       return Enhancer.create(targetClass, methodInterceptor);
    }
}
