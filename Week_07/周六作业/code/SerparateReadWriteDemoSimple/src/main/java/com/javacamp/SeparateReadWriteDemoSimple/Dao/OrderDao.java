package com.javacamp.SeparateReadWriteDemoSimple.Dao;

import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;

import java.sql.Connection;
import java.sql.SQLException;

public interface OrderDao {
    Order getOrderById(Connection conn, long orderId) throws SQLException;
    boolean insertOrder(Connection conn, Order order) throws SQLException;
}
