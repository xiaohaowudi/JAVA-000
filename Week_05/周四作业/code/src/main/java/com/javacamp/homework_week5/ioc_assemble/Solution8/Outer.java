package com.javacamp.homework_week5.ioc_assemble.Solution8;

/*
方案8 使用@Resource注解进行字段注入
 */

import com.javacamp.homework_week5.ioc_assemble.InnerInterface;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("outerObjSolution8")
public class Outer {
    @Resource(name= "innerImplAutowire")
    private InnerInterface inner;

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        inner.func();
    }
}
