package com.javacamp.homework_week5.ioc_assemble.Solution3;
import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;

/*
装配方案3：
XML 配置 beans 标签中 default-autowire 为byName, 按照对象名进行自动装配
 */

public class Outer {
    // Spring进行自动注入
    private InnerInterface innerObj;

    public void setInnerObj(InnerInterface inner) {
        this.innerObj = inner;
    }

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        innerObj.func();
    }

}
