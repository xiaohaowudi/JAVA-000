package com.javacamp.homework5.multi_xml_configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestMultiXmlConfiguration {

    @Test
    public void testMultiXmlConf() {

        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-multi-conf.xml");
            context.start();

            System.out.println(context.getBean("student1"));
            System.out.println(context.getBean("student2"));
            System.out.println(context.getBean("student3"));
            System.out.println(context.getBean("student4"));
            System.out.println(context.getBean("student5"));

            Klass klass1 = (Klass) context.getBean("klass1");
            klass1.dong();

            Klass klass2 = (Klass) context.getBean("klass2");
            klass2.dong();

            ISchool school = (ISchool) context.getBean("school");
            school.ding();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
