package com.javacamp.my_cache.annotation;

// 自定义注解，用该注解修饰的方法自动缓存结果seconds秒

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyCache {
    int value() default 60;
}
