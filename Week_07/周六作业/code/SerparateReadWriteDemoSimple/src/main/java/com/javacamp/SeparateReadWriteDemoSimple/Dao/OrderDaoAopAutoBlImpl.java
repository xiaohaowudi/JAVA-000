package com.javacamp.SeparateReadWriteDemoSimple.Dao;


import com.javacamp.SeparateReadWriteDemoSimple.Annotation.ReadOnlyOperation;
import com.javacamp.SeparateReadWriteDemoSimple.Annotation.WriteOperation;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

// 基于AOP实现自动切换数据源负载均衡的DAO
@Component("orderDaoAopAutoBl")
public class OrderDaoAopAutoBlImpl extends OrderDaoImpl implements OrderDao {

    @Override
    @ReadOnlyOperation
    public Order getOrderById(Connection conn, long orderId) throws SQLException {
        return super.getOrderById(conn, orderId);
    }

    @Override
    @WriteOperation
    public boolean insertOrder(Connection conn, Order order) throws SQLException {
        return super.insertOrder(conn, order);
    }
}
