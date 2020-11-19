package com.javacamp.homework5.database_operation.dao;

import com.javacamp.homework5.database_operation.model.Student;

import java.sql.SQLException;
import java.util.List;

// 拓展StudentDao接口，支持连接池，批处理, 事务，PrepareStatement
public interface StudentDaoExt extends StudentDao {

    // 批量添加学生信息
    boolean batchAdd(Student[] students) throws SQLException;

    // 根据id列表批量删除学生
    boolean batchDelById(int[] ids) throws SQLException;
}
