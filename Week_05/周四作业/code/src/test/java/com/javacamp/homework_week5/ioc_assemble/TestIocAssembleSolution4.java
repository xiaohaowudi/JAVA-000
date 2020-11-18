package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.UnitTestBase;
import org.junit.Test;

public class TestIocAssembleSolution4 extends UnitTestBase {

    public TestIocAssembleSolution4() {
        super("classpath*:spring-assemble-solution4.xml");
    }

    @Test
    public void testSolution() {
        com.javacamp.homework_week5.ioc_assemble.Solution4.Outer outer = getBean("outerObjSolution4");
        outer.func();
    }
}
