package io.kimmking.dubbo.demo.core.db_oper;

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
public class MySqlOperUtil {
    private final DataSource dataSource;

    public MySqlOperUtil(String databaseUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("connectionTimeout", "1000");
        config.addDataSourceProperty("idleTimeout", "6000");
        config.addDataSourceProperty("maximumPoolSize", "5");
        dataSource = new HikariDataSource(config);
    }

    // 批量执行sql, 返回每一个sql影响的记录数
    public int[] execSqlInTransaction(List<String> sqls) {
        for (String s : sqls) {
            System.err.println(s);
        }


        int[] ret = null;

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return ret;
        }

        try {
            conn.setAutoCommit(false);
            Statement stat = conn.createStatement();
            for (String sql : sqls) {
                stat.addBatch(sql);
            }
            ret = stat.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            ret = null;
            e.printStackTrace();
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

        return ret;
    }
}
