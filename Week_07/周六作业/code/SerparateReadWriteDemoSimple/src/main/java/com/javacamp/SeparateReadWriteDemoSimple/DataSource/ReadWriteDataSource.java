package com.javacamp.SeparateReadWriteDemoSimple.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

// 可读可写数据源接口
public interface ReadWriteDataSource {
    Connection getRwConnection() throws SQLException;
}
