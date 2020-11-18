package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.UnitTestBase;
import org.junit.Test;

// 测试XML配置使用构造方法进行注入
public class TestIocAssembleSolution3 extends UnitTestBase {

    public TestIocAssembleSolution3() {
        super("classpath*:spring-assemble-solution3.xml");
    }

    @Test
    public void testSolution() {
        com.javacamp.homework_week5.ioc_assemble.Solution3.Outer outer = getBean("outerObjSolution3");
        outer.func();
    }
}
