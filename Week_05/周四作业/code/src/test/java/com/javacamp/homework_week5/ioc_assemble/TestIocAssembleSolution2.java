package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.UnitTestBase;
import com.javacamp.homework_week5.ioc_assemble.Solution1.Outer;
import org.junit.Test;

// 测试XML配置使用构造方法进行注入
public class TestIocAssembleSolution2 extends UnitTestBase {

    public TestIocAssembleSolution2() {
        super("classpath*:spring-assemble-solution2.xml");
    }

    @Test
    public void testSolution() {
        com.javacamp.homework_week5.ioc_assemble.Solution2.Outer outer = getBean("outerObjSolution2");
        outer.func();
    }
}
