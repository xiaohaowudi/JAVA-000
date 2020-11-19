package com.javacamp.homework5.database_operation.dao;

import com.javacamp.homework5.database_operation.model.Student;

import java.sql.SQLException;
import java.util.List;

// 普通DAO 接口
public interface StudentDao {
    void addStudent(Student stu) throws SQLException;
    void delStudentById(int id) throws SQLException;
    void delAll() throws SQLException;
    void updateNameById(int id, String name) throws SQLException;
    List<Student> queryAll() throws SQLException;


}
