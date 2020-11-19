package com.javacamp.homework5.database_operation.dao;

import com.javacamp.homework5.database_operation.model.Student;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

// 基于Hikari线程池实现StudentDao
public class StudentDaoExtImplHikari implements StudentDaoExt {

    private DataSource dataSource;
    private String tblName;

    public StudentDaoExtImplHikari(String databaseUrl, String tblName, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("connectionTimeout", "1000");
        config.addDataSourceProperty("idleTimeout", "6000");
        config.addDataSourceProperty("maximumPoolSize", "5");
        dataSource = new HikariDataSource(config);

        this.tblName = tblName;
    }


    private boolean batchExec(Connection conn, PreparedStatement ps) throws SQLException {
        conn.setAutoCommit(false);
        boolean success = true;

        try {
            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            // 事务执行异常，进行回滚
            e.printStackTrace();
            conn.rollback();
            success = false;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }

        return success;
    }

    @Override
    public boolean batchAdd(Student[] students) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement stat = conn.prepareStatement("insert into " + tblName + " (id, name) values (?,?);");

        for (Student stu : students) {
            stat.setInt(1, stu.getId());
            stat.setString(2, stu.getName());
            stat.addBatch();
        }
        return batchExec(conn, stat);
    }

    @Override
    public boolean batchDelById(int[] ids) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement stat = conn.prepareStatement("delete from " + tblName + " where id = ?;");

        for (int id : ids) {
            stat.setInt(1, id);
            stat.addBatch();
        }
        return batchExec(conn, stat);
    }

    @Override
    public void addStudent(Student stu) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement stat = conn.prepareStatement("insert into " + tblName + " (id, name) values (?,?);");
        stat.setInt(1, stu.getId());
        stat.setString(2,stu.getName());
        stat.execute();

        conn.close();
    }

    @Override
    public void delStudentById(int id) throws SQLException {
        Connection conn = dataSource.getConnection();

        PreparedStatement stat = null;
        if (id == 0) {
            stat = conn.prepareStatement("delete from " + tblName);
        } else {
            stat = conn.prepareStatement("delete from " + tblName + " where id = ?;");
            stat.setInt(1, id);
        }

        stat.execute();
        conn.close();
    }

    @Override
    public void delAll() throws SQLException {
        delStudentById(0);
    }

    @Override
    public void updateNameById(int id, String name) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement stat = conn.prepareStatement("update " + tblName + " set name = ? where id = ?;");
        stat.setString(1, name);
        stat.setInt(2, id);
        stat.execute();

        conn.close();
    }

    @Override
    public List<Student> queryAll() throws SQLException {
        String sql = "select * from " + tblName;
        Connection conn = dataSource.getConnection();
        PreparedStatement stat = conn.prepareStatement("select * from " + tblName + ";");

        ResultSet rs = stat.executeQuery(sql);
        List<Student> ret = new LinkedList<>();

        while (rs.next()) {
            ret.add(new Student(rs.getInt("id"), rs.getString("name")));
        }

        conn.close();

        return ret;
    }
}
