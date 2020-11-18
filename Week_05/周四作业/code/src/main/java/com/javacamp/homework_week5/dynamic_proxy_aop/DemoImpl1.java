package com.javacamp.homework_week5.dynamic_proxy_aop;


import javax.xml.transform.Source;

// 业务接口实现类, 模拟实现业务
public class DemoImpl1 implements DemoInterface {
    @Override
    public void func() {
        System.out.println("This is DemoImpl1 func");
    }
}
