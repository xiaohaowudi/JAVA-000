package io.kimmking.rpcfx.client;


import java.lang.annotation.*;


// 自定义远端调用注解

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteService {
    String value() default "";
}
