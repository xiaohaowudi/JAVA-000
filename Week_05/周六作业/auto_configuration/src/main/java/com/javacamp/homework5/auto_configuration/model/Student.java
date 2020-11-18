package com.javacamp.homework5.auto_configuration.model;


import java.io.Serializable;

public class Student implements Serializable {

    private int id;
    private String name;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
