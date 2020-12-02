package com.javacamp.SeparateReadWriteDemoSimple.Configuration;


import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderDaoConfiguration {
    @Bean
    public OrderDao orderDao() {
        return new OrderDaoImpl();
    }
}
