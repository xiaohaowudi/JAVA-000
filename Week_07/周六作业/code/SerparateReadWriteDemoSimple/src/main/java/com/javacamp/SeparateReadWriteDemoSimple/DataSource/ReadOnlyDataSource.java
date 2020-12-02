package com.javacamp.SeparateReadWriteDemoSimple.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

// 只读数据源接口
public interface ReadOnlyDataSource {
    Connection getRoConnection() throws SQLException;
}
