package com.javacamp.homework_week5.ioc_assemble.Solution1;

import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;

/*
装配方案1：
在XML中配置inner字段用setter方法进行注入，对应xml文件为spring-assemble-solution1.xml

 */

public class Outer {

    private InnerInterface inner;

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        inner.func();
    }

    public void setInner(InnerImpl inner) {
        this.inner = inner;
    }
}
