package com.javacamp.insert_order_test.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OrderInsertDaoBase {

    protected String tblName;
    protected String databaseUrl;
    protected String username;
    protected String password;
    protected Connection conn;

    public OrderInsertDaoBase(String databaseUrl, String tblName, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.databaseUrl = databaseUrl;
        this.tblName = tblName;
        this.username = username;
        this.password = password;
        this.conn = DriverManager.getConnection(databaseUrl, username, password);
    }

}
