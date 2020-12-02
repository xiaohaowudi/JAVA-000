package com.javacamp.SeparateReadWriteDemoSimple.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HiKariReadWriteDataSourceImpl extends HikariDataSourceBase implements ReadWriteDataSource {

    public HiKariReadWriteDataSourceImpl(String databaseUrl, String username, String password) {
        super(databaseUrl, username, password);
    }

    @Override
    public Connection getRwConnection() throws SQLException {
        return getConnection();
    }
}
