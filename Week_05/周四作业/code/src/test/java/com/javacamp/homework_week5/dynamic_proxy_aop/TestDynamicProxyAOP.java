package com.javacamp.homework_week5.dynamic_proxy_aop;


import org.junit.jupiter.api.Test;

public class TestDynamicProxyAOP {
    @Test
    public void testDynamicProxyAOP() {
        DemoInterface obj1 = new DemoImpl1();
        DemoInterface obj2 = new DemoImpl2();

        DemoInterface aopObj1 = (DemoInterface)DynamicProxyAOP.getProxy(obj1, DemoInterface.class, new DemoInterceptor());
        DemoInterface aopObj2 = (DemoInterface)DynamicProxyAOP.getProxy(obj2, DemoInterface.class, new DemoInterceptor());

        aopObj1.func();
        aopObj2.func();
    }

}
