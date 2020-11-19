package com.javacamp.homework5.database_operation.dao;


import com.javacamp.homework5.database_operation.model.Student;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

// JDBC原生接口实现CRUD
public class StudentDaoImplOriginalJdbc implements StudentDao  {

    private String tblName;
    private String databaseUrl;
    private String username;
    private String password;

    private Connection getConnectioin() throws SQLException {
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public StudentDaoImplOriginalJdbc(String databaseUrl, String tblName, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.databaseUrl = databaseUrl;
        this.tblName = tblName;
        this.username = username;
        this.password = password;
    }

    @Override
    public void addStudent(Student stu) throws SQLException {
        String sql = "insert into " + tblName + " (id, name) values (" + stu.getId() + "," + "'" + stu.getName() + "'" + ")";
        Connection conn = getConnectioin();
        Statement statement = conn.createStatement();

        statement.executeUpdate(sql);

        statement.close();
        conn.close();
    }

    @Override
    public void delStudentById(int id) throws SQLException {
        String sql = id != 0 ? "delete from " + tblName + " where id = " + id : "delete from " + tblName;

        Connection conn = getConnectioin();
        Statement statement = conn.createStatement();

        statement.executeUpdate(sql);

        statement.close();
        conn.close();
    }

    @Override
    public void delAll() throws SQLException {
        delStudentById(0);
    }

    @Override
    public void updateNameById(int id, String name) throws SQLException {
        String sql = "update " + tblName + " set name = " + "'" + name + "'" + " where id = " + id;
        Connection conn = getConnectioin();
        Statement statement = conn.createStatement();

        statement.executeUpdate(sql);

        statement.close();
        conn.close();
    }

    @Override
    public List<Student> queryAll() throws SQLException {
        String sql = "select * from " + tblName;
        Connection conn = getConnectioin();
        Statement statement = conn.createStatement();

        ResultSet rs = statement.executeQuery(sql);
        List<Student> ret = new LinkedList<>();

        while (rs.next()) {
            ret.add(new Student(rs.getInt("id"), rs.getString("name")));
        }

        statement.close();
        conn.close();

        return ret;
    }
}
