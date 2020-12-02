package com.javacamp.insert_order_test.dao;

import com.javacamp.insert_order_test.dao.OrderInsertDao;
import com.javacamp.insert_order_test.dao.OrderInsertDaoBase;
import com.javacamp.insert_order_test.model.Order;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// 插入语句批量分多次事务提交方案实现
public class OrderInsertDaoBatchSingleTxImpl extends OrderInsertDaoBase implements OrderInsertDao {
    private int batchSize;      // 批次大小

    public OrderInsertDaoBatchSingleTxImpl(String databaseUrl, String tblName, String username, String password, int batchSize) throws ClassNotFoundException, SQLException {
        super(databaseUrl, tblName, username, password);
        this.batchSize = batchSize;

    }

    @Override
    public void insertOrders(Order[] orders) throws SQLException {
        System.out.println("insert table, batchSize = " + batchSize);
        try {
            conn.setAutoCommit(false);

            int n = orders.length;
            int pos = 0;

            while (pos < n) {

                int batchExeSize = Integer.min(batchSize, n - pos);

                PreparedStatement stat = conn.prepareStatement("insert into " + tblName + " (order_id, supplier_user_id, customer_user_id, status, trade_no, create_time_stamp, modify_time_stamp) values (?,?,?,?,?,?,?);");
                for (int i = pos; i < pos + batchExeSize; i++) {
                    stat.setLong(1, 0);
                    stat.setLong(2, orders[i].getSellerId());
                    stat.setLong(3, orders[i].getCustomerId());
                    stat.setByte(4, orders[i].getOrderState());
                    stat.setString(5, orders[i].getTradeNo());
                    stat.setLong(6, orders[i].getCreateTimeStamp());
                    stat.setLong(7, orders[i].getModifyTimeStamp());
                    stat.addBatch();
                }
                pos += batchExeSize;

                stat.executeBatch();
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
