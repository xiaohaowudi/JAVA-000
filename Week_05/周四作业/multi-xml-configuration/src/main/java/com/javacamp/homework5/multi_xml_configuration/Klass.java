package com.javacamp.homework5.multi_xml_configuration;

import java.util.List;

public class Klass {

    List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void dong(){
        System.out.println(this.getStudents());
    }

    public void setStudents(List students) {
        this.students = students;
    }
}