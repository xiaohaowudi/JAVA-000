package com.javacamp.byte_buddy_aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;
import java.util.logging.Logger;
import static net.bytebuddy.matcher.ElementMatchers.*;

// 打印函数运行时间的AOP
public class LogTimeAOP {

    private static Logger logger = Logger.getLogger(LogTimeAOP.class.getName());

    // 统计方法运行时间，简单实现AOP
    public static Integer logTime(@SuperCall Callable<Integer> innerCall) throws Exception {
        long start = System.currentTimeMillis();
        Integer ret = innerCall.call();
        long interval = System.currentTimeMillis() - start;
        logger.info("method call spend " + interval + " ms");

        return ret;
    }

    // 构造增强AOP类的API
    public static Class loadLogTimeExpClass(Class cls) {
        return
        new ByteBuddy()
        .subclass(cls)
        .method(named("func"))
        .intercept(MethodDelegation.to(LogTimeAOP.class))
        .make()
        .load(LogTimeAOP.class.getClassLoader())
        .getLoaded();
    }
}
