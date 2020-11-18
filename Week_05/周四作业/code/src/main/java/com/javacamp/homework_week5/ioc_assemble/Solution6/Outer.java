package com.javacamp.homework_week5.ioc_assemble.Solution6;
import com.javacamp.homework_week5.ioc_assemble.InnerImpl;
import com.javacamp.homework_week5.ioc_assemble.InnerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/*
装配方案6
@Autowired 注解配置Setter进行注入
 */

@Component("outerObjSolution6")
public class Outer {
    private InnerInterface innerObj;

    @Autowired
    public void setInnerObj(InnerInterface inner) {
        this.innerObj = inner;
    }

    public void func() {
        System.out.println("testing " + this.getClass().getName());
        innerObj.func();
    }
}

