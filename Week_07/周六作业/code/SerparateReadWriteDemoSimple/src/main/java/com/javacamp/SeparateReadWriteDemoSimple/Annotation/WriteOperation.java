package com.javacamp.SeparateReadWriteDemoSimple.Annotation;

import java.lang.annotation.*;

// 限定方法对数据库写入的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WriteOperation {
}
