package com.javacamp.homework_week5.ioc_assemble.Solution4;
import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;

/*
装配方案4：
XML 配置 beans 标签中 default-autowire 为constructor, 用Outer的构造函数进行注入
 */

public class Outer {
    // Spring进行自动注入
    private InnerInterface innerObj;

    public Outer(InnerInterface inner) {
        this.innerObj = inner;
    }

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        innerObj.func();
    }

}
