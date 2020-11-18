package com.javacamp.homework5.auto_configuration.bootstrap;

import com.javacamp.homework5.auto_configuration.model.Klass;
import com.javacamp.homework5.auto_configuration.model.School;
import com.javacamp.homework5.auto_configuration.model.Student;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@EnableAutoConfiguration
public class AutoConfigurationBootStrap {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AutoConfigurationBootStrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        try {
            Student stu1 = context.getBean("student1", Student.class);
            Student stu2 = context.getBean("student2", Student.class);
            Klass klass = context.getBean("klass", Klass.class);
            School school = context.getBean("school", School.class);

            System.out.println(stu1);
            System.out.println(stu2);
            klass.dong();
            school.ding();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }

}
