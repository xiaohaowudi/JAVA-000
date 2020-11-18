package com.javacamp.homework_week5.ioc_assemble.Solution5;
import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/*
装配方案5
@Autowired 注解配置字段注入
 */

@Component("outerObjSolution5")
public class Outer {
    @Autowired
    private InnerInterface innerObj;

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        innerObj.func();
    }
}
