package com.javacamp.homework5.auto_configuration.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class School implements ISchool {
    @Autowired
    Klass class1;

    @Resource(name = "student1")
    Student student1;

    @Override
    public void ding(){
        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student1);
    }
}
