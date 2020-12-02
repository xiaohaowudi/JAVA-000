package com.javacamp.SeparateReadWriteDemoSimple.Configuration;


import com.javacamp.SeparateReadWriteDemoSimple.DataSource.HikariReadOnlyDataSourceImpl;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.HiKariReadWriteDataSourceImpl;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadOnlyDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadWriteDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 一个只读数据源
@Configuration
public class DataSourceConfiguration {

    @Bean
    public ReadOnlyDataSource readOnlyDataSource1() {
        return new HikariReadOnlyDataSourceImpl("jdbc:mysql://192.168.3.148/test?useServerPrepStmts=true", "root", "123456");
    }

    @Bean
    public ReadOnlyDataSource readOnlyDataSource2() {
        return new HikariReadOnlyDataSourceImpl("jdbc:mysql://192.168.3.147/test?useServerPrepStmts=true", "root", "123456");
    }

    @Bean
    public ReadOnlyDataSource readOnlyDataSource3() {
        return new HikariReadOnlyDataSourceImpl("jdbc:mysql://192.168.3.146/test?useServerPrepStmts=true", "root", "123456");
    }

    @Bean
    public ReadOnlyDataSource readOnlyDataSource4() {
        return new HikariReadOnlyDataSourceImpl("jdbc:mysql://192.168.3.145/test?useServerPrepStmts=true", "root", "123456");
    }


    @Bean
    public ReadWriteDataSource readWriteDataSource() {
        return new HiKariReadWriteDataSourceImpl("jdbc:mysql://192.168.3.144/test?useServerPrepStmts=true", "root", "123456");
    }
}
