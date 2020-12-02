package com.javacamp.insert_order_test.dao;

import com.javacamp.insert_order_test.model.Order;

import java.sql.SQLException;

public interface OrderInsertDao {

    void insertOrders(Order[] orders) throws SQLException;
}
