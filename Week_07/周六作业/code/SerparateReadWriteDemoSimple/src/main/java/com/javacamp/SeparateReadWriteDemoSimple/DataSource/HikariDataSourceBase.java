package com.javacamp.SeparateReadWriteDemoSimple.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariDataSourceBase {
    private DataSource dataSource;

    public HikariDataSourceBase(String databaseUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("connectionTimeout", "1000");
        config.addDataSourceProperty("idleTimeout", "6000");
        config.addDataSourceProperty("maximumPoolSize", "5");
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
