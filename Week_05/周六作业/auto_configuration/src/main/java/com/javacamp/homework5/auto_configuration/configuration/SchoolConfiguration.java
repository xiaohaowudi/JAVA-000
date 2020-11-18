package com.javacamp.homework5.auto_configuration.configuration;

import com.javacamp.homework5.auto_configuration.model.Klass;
import com.javacamp.homework5.auto_configuration.model.School;
import com.javacamp.homework5.auto_configuration.model.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchoolConfiguration {

    @Bean
    public Student student1() {
        return new Student(100, "Jamse");
    }

    @Bean
    public Student student2() {
        return new Student(200, "Tom");
    }

    @Bean
    public School school() {
        return new School();
    }

    @Bean
    public Klass klass() {
        return new Klass();
    }
}
