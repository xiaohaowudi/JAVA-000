package com.javacamp.SeparateReadWriteDemoSimple.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariReadOnlyDataSourceImpl extends HikariDataSourceBase implements ReadOnlyDataSource {

    public HikariReadOnlyDataSourceImpl(String databaseUrl, String username, String password) {
        super(databaseUrl, username, password);
    }

    @Override
    public Connection getRoConnection() throws SQLException {
        return getConnection();
    }
}
