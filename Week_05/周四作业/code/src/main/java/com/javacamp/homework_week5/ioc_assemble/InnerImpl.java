package com.javacamp.homework_week5.ioc_assemble;

import javax.xml.transform.Source;

// 被注入的具体实现类
public class InnerImpl implements InnerInterface {

    @Override
    public void func() {
        System.out.println("InnerImpl func");
    }
}
