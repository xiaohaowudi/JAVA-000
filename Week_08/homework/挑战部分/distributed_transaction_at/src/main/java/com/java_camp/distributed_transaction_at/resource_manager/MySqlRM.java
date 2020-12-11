package com.java_camp.distributed_transaction_at.resource_manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

// MySql 数据源，实现RM接口
@Log
public class MySqlRM implements ResourceManager {
    private final DataSource dataSource;
    private String rmName;


    public MySqlRM(String name, String databaseUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        rmName = name;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("connectionTimeout", "1000");
        config.addDataSourceProperty("idleTimeout", "6000");
        config.addDataSourceProperty("maximumPoolSize", "5");
        dataSource = new HikariDataSource(config);
    }

    @Override
    public boolean atExecuteBranchTransaction(List<String> sqls) {
        boolean isSuccess = true;

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        try {
            conn.setAutoCommit(false);
            Statement stat = conn.createStatement();
            for (String sql : sqls) {
                stat.addBatch(sql);
            }
            stat.executeBatch();

            // tcc模式下，分支事务直接提交
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            isSuccess = false;

            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return isSuccess;
    }

    @Override
    public String getRmName() {
        return this.rmName;
    }
}
