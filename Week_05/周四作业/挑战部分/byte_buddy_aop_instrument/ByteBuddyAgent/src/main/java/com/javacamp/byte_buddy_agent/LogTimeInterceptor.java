package com.javacamp.byte_buddy_agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

// 记录函数运行时间的拦截器实现
public class LogTimeInterceptor {
    // 统计方法运行时间，简单实现AOP
    @RuntimeType
    public static Object logTime(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {

        System.out.println("############ this is in agent begin ###############");
        long start = System.currentTimeMillis();
        try {
            return callable.call();
        } finally {
            System.out.println(method + ": spend " + (System.currentTimeMillis() - start) + "ms");
            System.out.println("############ this is in agent end ###############");
        }
    }

}
