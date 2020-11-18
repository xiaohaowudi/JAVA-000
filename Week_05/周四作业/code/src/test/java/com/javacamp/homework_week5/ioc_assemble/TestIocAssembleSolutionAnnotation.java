package com.javacamp.homework_week5.ioc_assemble;

import com.javacamp.homework_week5.UnitTestBase;
import org.junit.Test;

public class TestIocAssembleSolutionAnnotation extends UnitTestBase {

    public TestIocAssembleSolutionAnnotation() {
        super("classpath*:spring-assemble-solution-annotation.xml");
    }

    @Test
    public void testSolution5() {
        com.javacamp.homework_week5.ioc_assemble.Solution5.Outer outer = getBean("outerObjSolution5");
        outer.func();
    }

    @Test
    public void testSolution6() {
        com.javacamp.homework_week5.ioc_assemble.Solution6.Outer outer = getBean("outerObjSolution6");
        outer.func();
    }

    @Test
    public void testSolution7() {
        com.javacamp.homework_week5.ioc_assemble.Solution7.Outer outer = getBean("outerObjSolution7");
        outer.func();
    }

    @Test
    public void testSolution8() {
        com.javacamp.homework_week5.ioc_assemble.Solution8.Outer outer = getBean("outerObjSolution8");
        outer.func();
    }

    @Test
    public void testSolution9() {
        com.javacamp.homework_week5.ioc_assemble.Solution9.Outer outer = getBean("outerObjSolution9");
        outer.func();
    }

}
