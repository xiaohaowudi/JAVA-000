package com.javacamp.insert_order_test.dao;
import com.javacamp.insert_order_test.model.Order;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// 每条所有插入语句构成一个大事务提交方案
public class OrderInsertDaoNoBatchSingleTxImpl extends OrderInsertDaoBase implements OrderInsertDao {

    public OrderInsertDaoNoBatchSingleTxImpl(String databaseUrl, String tblName, String username, String password) throws ClassNotFoundException, SQLException {
        super(databaseUrl, tblName, username, password);
    }

    @Override
    public void insertOrders(Order[] orders) throws SQLException {

        try {
            conn.setAutoCommit(false);

            PreparedStatement stat = conn.prepareStatement("insert into " + tblName + " (order_id, supplier_user_id, customer_user_id, status, trade_no, create_time_stamp, modify_time_stamp) values (?,?,?,?,?,?,?);");
            for (Order order : orders) {
                stat.setLong(1, 0);
                stat.setLong(2, order.getSellerId());
                stat.setLong(3, order.getCustomerId());
                stat.setByte(4, order.getOrderState());
                stat.setString(5, order.getTradeNo());
                stat.setLong(6, order.getCreateTimeStamp());
                stat.setLong(7, order.getModifyTimeStamp());

                stat.execute();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
