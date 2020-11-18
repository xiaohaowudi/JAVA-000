package com.javacamp.homework_week5.ioc_assemble.Solution9;


/*
方案9 使用@Resource注解进行setter注入
 */

import com.javacamp.homework_week5.ioc_assemble.InnerInterface;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("outerObjSolution9")
public class Outer {
    private InnerInterface inner;

    @Resource(name="innerImplAutowire")
    public void setInner(InnerInterface inner) {
        this.inner = inner;
    }

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        inner.func();
    }
}
