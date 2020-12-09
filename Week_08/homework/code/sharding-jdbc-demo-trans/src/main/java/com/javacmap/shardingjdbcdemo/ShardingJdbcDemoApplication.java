package com.javacmap.shardingjdbcdemo;

import org.aspectj.weaver.SourceContextImpl;
import org.aspectj.weaver.ast.Or;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = JtaAutoConfiguration.class)
@MapperScan("com.javacmap.shardingjdbcdemo.dao")
public class ShardingJdbcDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShardingJdbcDemoApplication.class, args);
    }

}
