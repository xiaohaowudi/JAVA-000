package com.javacamp.my_cache.aspect;

import com.javacamp.my_cache.annotation.MyCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Component
@Aspect
public class MyCacheAspect {

    private Logger logger = Logger.getLogger(String.valueOf(this.getClass()));

    // 调用函数名和调用对象的哈希值拼接字符串作为key，数值是每个函数调用的计数值
    // 计数值每秒增加1，用于判断当前缓存的数值是否过时
    private ConcurrentMap<String, AtomicInteger> callInfo2Cnt = new ConcurrentHashMap<String, AtomicInteger>();

    // 调用函数名和调用对象的哈希值拼接字符串作为key，数值是每个函数调用结果的缓存值
    private ConcurrentMap<String, Object> callInfo2Val = new ConcurrentHashMap<String, Object>();

    public MyCacheAspect() {
        // 启动定时器更新callInfo2Cnt
        new Thread(() -> {
            while (true) {
                for (AtomicInteger cnt : callInfo2Cnt.values()) {
                    cnt.incrementAndGet();
                }

                System.err.println(callInfo2Cnt);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 切入点设置拦截所有被MyCache注解修饰的函数
    @Pointcut("@annotation(com.javacamp.my_cache.annotation.MyCache)")
    //@Pointcut("execution(* com.javacamp.my_cache.MyCacheApplication.getValCache10(..))")
    public void point() {

    }

    @Around("com.javacamp.my_cache.aspect.MyCacheAspect.point()")
    public Object cacheGetVal(ProceedingJoinPoint pjp) {
        Class<?> targetClass = pjp.getTarget().getClass();
        String methodName = pjp.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature)(pjp.getSignature())).getParameterTypes();


        String callInfo = pjp.getTarget().hashCode() + "#" + methodName;
        if (!callInfo2Cnt.containsKey(callInfo)) {
            callInfo2Cnt.put(callInfo, new AtomicInteger(0x7fffffff));    // 初始时候设置超时，触发读取最新数值
            callInfo2Val.put(callInfo, 0);
        }

        Object ret = null;
        try {
            Method method = targetClass.getMethod(methodName, parameterTypes);
            MyCache anno = method.getAnnotation(MyCache.class);

            int seconds = anno.value();
            int curCnt = callInfo2Cnt.get(callInfo).get();

            if (curCnt >= seconds) {
                // 缓存超时, 调用真正的函数获取最新数值
                logger.info("cache time out !!!");
                ret = pjp.proceed();
                callInfo2Val.put(callInfo, ret);
                callInfo2Cnt.get(callInfo).set(0);      // 重置计数值
            } else {
                ret = callInfo2Val.get(callInfo);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return ret;
    }
}
