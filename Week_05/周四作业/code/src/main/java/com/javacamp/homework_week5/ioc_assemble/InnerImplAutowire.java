package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.ioc_assemble.InnerInterface;
import org.springframework.stereotype.Component;

@Component
public class InnerImplAutowire implements InnerInterface {

    @Override
    public void func() {
        System.out.println("InnerImplAutowire func");
    }
}
