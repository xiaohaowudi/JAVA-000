package com.javacamp.homework5.database_operation;

import com.javacamp.homework5.database_operation.dao.StudentDao;
import com.javacamp.homework5.database_operation.dao.StudentDaoExt;
import com.javacamp.homework5.database_operation.dao.StudentDaoExtImplHikari;
import com.javacamp.homework5.database_operation.dao.StudentDaoImplOriginalJdbc;
import com.javacamp.homework5.database_operation.model.Student;
import org.junit.jupiter.api.Test;

import javax.xml.transform.Source;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TestDatabaseOperation {

    // 测试原生JDBC的CRUD
    @Test
    public void testOriginalCrud() throws SQLException, ClassNotFoundException {
        try {

            Student stu1 = new Student(100, "Jamse1");
            Student stu2 = new Student(200, "Jamse2");
            Student stu3 = new Student(300, "Jamse3");
            Student stu4 = new Student(400, "Jamse4");
            Student stu5 = new Student(500, "Jamse5");

            StudentDao dao = new StudentDaoImplOriginalJdbc("jdbc:mysql://192.168.3.200/test?useServerPrepStmts=true", "student", "root", "123456");
            dao.delAll();

            dao.addStudent(stu1);
            dao.addStudent(stu2);
            dao.addStudent(stu3);
            dao.addStudent(stu4);
            dao.addStudent(stu5);

            dao.updateNameById(100, "Mike1");
            dao.updateNameById(200, "Mike2");

            dao.delStudentById(300);

            List<Student> students = dao.queryAll();
            System.out.println("query all result:");
            System.out.println(students);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试基于Hikari连接池的Dao功能
    @Test
    public void testHikariCrud() throws SQLException, ClassNotFoundException {
        try {

            Student stu1 = new Student(100, "Jamse1");
            Student stu2 = new Student(200, "Jamse2");
            Student stu3 = new Student(300, "Jamse3");
            Student stu4 = new Student(400, "Jamse4");
            Student stu5 = new Student(500, "Jamse5");

            StudentDaoExt dao = new StudentDaoExtImplHikari("jdbc:mysql://192.168.3.200/test?useServerPrepStmts=true", "student", "root", "123456");
            dao.delAll();

            dao.addStudent(stu1);
            dao.addStudent(stu2);
            dao.addStudent(stu3);
            dao.addStudent(stu4);
            dao.addStudent(stu5);

            boolean ret;

            // 事务批量添加信息，故意构造事务失败
            ret = dao.batchAdd(new Student[] {new Student(600, "Harry6"), new Student(100, "Harry1"), new Student(800, "Harry8")});
            if (!ret) {
                System.err.println("batch add operation failed!");
            }

            // 事务批量成功添加信息
            ret = dao.batchAdd(new Student[] {new Student(900, "Harry9"), new Student(1000, "Harry10"), new Student(1100, "Harry11")});
            if (!ret) {
                System.err.println("batch add operation failed!");
            }

            // 事务批量删除信息
            ret = dao.batchDelById(new int[] {500, 900});
            if (!ret) {
                System.err.println("batch del operation failed!");
            }


            dao.updateNameById(100, "Mike1");
            dao.updateNameById(200, "Mike2");

            dao.delStudentById(300);

            List<Student> students = dao.queryAll();
            System.out.println("query all result:");
            System.out.println(students);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
