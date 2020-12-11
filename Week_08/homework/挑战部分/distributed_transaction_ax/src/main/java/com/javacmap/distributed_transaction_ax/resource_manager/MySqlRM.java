package com.javacmap.distributed_transaction_ax.resource_manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// MySql 数据源，实现RM接口
@Log
public class MySqlRM implements ResourceManager {

    private final ConcurrentHashMap<Long, Connection> xid2conn;     // xid到数据库连接的映射
    private final DataSource dataSource;
    private String rmName;


    public MySqlRM(String name, String databaseUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        rmName = name;
        xid2conn = new ConcurrentHashMap<>();

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
    public boolean xaPrepare(long xid, List<String> sqls) {
        boolean isSuccess = true;

        Connection conn = null;
        try {
            synchronized (this) {
                // 存在相同编号的还没有commit的事务，返回失败
                if (xid2conn.containsKey(xid)) {
                    log.info("duplicate xid " + xid);
                    return false;
                }

                conn = dataSource.getConnection();
                xid2conn.put(xid, conn);
            }

            conn.setAutoCommit(false);
            Statement stat = conn.createStatement();
            for (String sql : sqls) {
                stat.addBatch(sql);
            }
            stat.executeBatch();


        } catch (SQLException e) {
            e.printStackTrace();
            isSuccess = false;

            if (conn != null) {
                try {
                    xid2conn.remove(xid);
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return isSuccess;
    }

    private boolean endTransaction(long xid, boolean isCommit) {
        boolean isSuccess = true;

        synchronized (this) {
            if (!xid2conn.containsKey(xid)) {
                log.info("invalid transaction xid = " + xid);
                return false;
            }

            Connection conn = xid2conn.get(xid);
            try {
                if (isCommit) {
                    log.info("processing commit, xid = " + xid + ", data source = " + getRmName());
                    conn.commit();
                } else {
                    log.info("processing rollback, xid = " + xid + ", data source = " + getRmName());
                    conn.rollback();
                }
            } catch (SQLException e) {
                isSuccess = false;
                e.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    xid2conn.remove(xid);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return isSuccess;
    }

    @Override
    public boolean xaCommit(long xid) {
        return endTransaction(xid, true);
    }

    @Override
    public boolean xaRollback(long xid) {
        return endTransaction(xid, false);
    }

    @Override
    public String getRmName() {
        return this.rmName;
    }
}
