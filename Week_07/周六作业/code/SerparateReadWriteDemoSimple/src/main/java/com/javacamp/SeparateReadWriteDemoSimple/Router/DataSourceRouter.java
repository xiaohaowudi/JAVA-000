package com.javacamp.SeparateReadWriteDemoSimple.Router;


import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadOnlyDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadWriteDataSource;

import javax.sql.DataSource;

// 数据源的路由选择接口
public interface DataSourceRouter {
    ReadOnlyDataSource getReadDataSource();
    ReadWriteDataSource getReadWriteDataSource();
}
