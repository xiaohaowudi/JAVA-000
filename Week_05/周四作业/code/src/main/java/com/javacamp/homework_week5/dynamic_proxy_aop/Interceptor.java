package com.javacamp.homework_week5.dynamic_proxy_aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 拦截器接口
public interface Interceptor {
    public void before(Object obj, Method method, Object[] args);       // 前向拦截接口
    public void after(Object obj, Method method, Object[] args);        // 后向拦截接口

    // 环绕拦截器接口，默认时间就是对方法进行调用
    default public Object around(Object obj, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, args);
    }
}
