package com.javacamp.homework5.multi_xml_configuration;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class School implements ISchool {
    Klass class1;
    Klass class2;

    @Override
    public void ding(){
        System.out.println("Class1 have " + this.class1.getStudents().size());
        System.out.println("Class2 have " + this.class2.getStudents().size());
    }

    public void setClass1(Klass class1) {
        this.class1 = class1;
    }

    public void setClass2(Klass class2) {
        this.class2 = class2;
    }
}
