package com.javacamp.homework_week5.dynamic_proxy_aop;


/*
动态代理实现简单的AOP
 */


import java.lang.reflect.Proxy;

public class DynamicProxyAOP  {
    public static Object getProxy (Object obj, Class interfaceClass, Interceptor interceptor) {
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                                      new Class[]{interfaceClass},
                                      new DynamicProxyHandler(obj, interceptor));
    }
}
