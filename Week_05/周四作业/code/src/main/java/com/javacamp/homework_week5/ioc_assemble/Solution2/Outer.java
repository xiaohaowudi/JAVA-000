package com.javacamp.homework_week5.ioc_assemble.Solution2;
import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;

/*
装配方案2：
在XML中配置inner字段用Outer类的构造方法进行注入，对应xml文件为spring-assemble-solution2.xml
 */


public class Outer {

    private InnerInterface inner;

    public Outer(InnerInterface inner) {
        this.inner = inner;
    }

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        inner.func();
    }

}
