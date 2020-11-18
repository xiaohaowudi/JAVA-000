package com.javacamp.homework5.auto_configuration.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Klass {

    @Autowired
    List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void dong(){
        System.out.println(this.getStudents());
    }

}