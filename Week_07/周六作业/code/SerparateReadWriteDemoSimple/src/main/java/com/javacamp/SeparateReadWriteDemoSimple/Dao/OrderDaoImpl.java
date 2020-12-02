package com.javacamp.SeparateReadWriteDemoSimple.Dao;

import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order getOrderById(Connection conn, long orderId) throws SQLException {
        Order order = null;
        try {
            PreparedStatement stat = conn.prepareStatement("select * from OrderBaseInfo where order_id = ?;");
            stat.setLong(1, orderId);

            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                order = new Order(
                        rs.getLong("order_id"),
                        rs.getLong("supplier_user_id"),
                        rs.getLong("customer_user_id"),
                        rs.getByte("status"),
                        rs.getString("trade_no"),
                        rs.getLong("create_time_stamp"),
                        rs.getLong("modify_time_stamp"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return order;
    }

    @Override
    public boolean insertOrder(Connection conn, Order order) throws SQLException {
        conn.setAutoCommit(true);
        boolean success = true;
        try {
            PreparedStatement stat = conn.prepareStatement("insert into OrderBaseInfo" + " (order_id, supplier_user_id, customer_user_id, status, trade_no, create_time_stamp, modify_time_stamp) values (?,?,?,?,?,?,?);");
            stat.setLong(1, 0);
            stat.setLong(2, order.getSellerId());
            stat.setLong(3, order.getCustomerId());
            stat.setByte(4, order.getOrderState());
            stat.setString(5, order.getTradeNo());
            stat.setLong(6, order.getCreateTimeStamp());
            stat.setLong(7, order.getModifyTimeStamp());

            stat.execute();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }
}
