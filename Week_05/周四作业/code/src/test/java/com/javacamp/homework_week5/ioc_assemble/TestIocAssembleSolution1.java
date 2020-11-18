package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.UnitTestBase;
import com.javacamp.homework_week5.ioc_assemble.Solution1.Outer;
import org.junit.Test;

// 测试XML配置用Setter进行注入
public class TestIocAssembleSolution1 extends UnitTestBase {

    public TestIocAssembleSolution1() {
        super("classpath*:spring-assemble-solution1.xml");
    }

    @Test
    public void testSolution() {
        com.javacamp.homework_week5.ioc_assemble.Solution1.Outer outer = getBean("outerObjSolution1");
        outer.func();
    }
}
