package com.javacamp.insert_order_test;


import com.javacamp.insert_order_test.dao.OrderInsertDao;
import com.javacamp.insert_order_test.dao.OrderInsertDaoBatchSingleTxImpl;
import com.javacamp.insert_order_test.dao.OrderInsertDaoNoBatchMultiTxImpl;
import com.javacamp.insert_order_test.dao.OrderInsertDaoNoBatchSingleTxImpl;
import com.javacamp.insert_order_test.model.Order;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@Log
public class TestOrderInsert {
    private Connection conn;

    TestOrderInsert() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://192.168.3.144/test?useServerPrepStmts=true", "root", "123456");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("TestOrderInsert initialization fail!!!");
        }
    }

    private Order[] genTestOrders(int orderNum) {
        if (orderNum <= 0) {
            throw new RuntimeException("parameter error");
        }

        Order[] orders = new Order[orderNum];
        for (int i = 0; i < orderNum; i++) {
            orders[i] = new Order(0, i, (byte) (i%2), "123456789###"+ i, System.currentTimeMillis(), System.currentTimeMillis());
        }
        return orders;
    }

    // 用例开始时候把表删掉，新建一个空表
    @BeforeEach
    private void initTable() {
        try {
            log.info("init table start");

            conn.setAutoCommit(false);
            PreparedStatement stat = conn.prepareStatement("drop table if exists OrderBaseInfo;");
            stat.execute();

            stat = conn.prepareStatement("CREATE TABLE IF NOT EXISTS OrderBaseInfo (\n" +
                    "    order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单id',\n" +
                    "    supplier_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '卖方用户id',\n" +
                    "    customer_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '买方用户id',\n" +
                    "    status TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '订单状态',\n" +
                    "    trade_no VARCHAR(100) NOT NULL DEFAULT '' COMMENT '支付交易号',\n" +
                    "\n" +
                    "    create_time_stamp BIGINT UNSIGNED NOT NULL COMMENT '创建订单时间戳',\n" +
                    "    modify_time_stamp BIGINT UNSIGNED NOT NULL COMMENT '订单基本信息变更时间戳',\n" +
                    "    PRIMARY KEY(order_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '订单表';");
            stat.execute();

            conn.commit();
            conn.setAutoCommit(true);

            log.info("init table over");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("init table fail");
        }
    }

    // 每一条insert语句一个事务场景测试

    @Test
    public void testMultiTxInsert() {
        log.info("testMultiTxInsert begin");

        try {
            OrderInsertDao insertDao = new OrderInsertDaoNoBatchMultiTxImpl("jdbc:mysql://192.168.3.144/test?useServerPrepStmts=true", "OrderBaseInfo", "root", "123456");
            Order[] orders = genTestOrders(1000000);

            long startTime = System.currentTimeMillis();
            insertDao.insertOrders(orders);
            long endTime = System.currentTimeMillis();

            log.info("testMultiTxInsert spend time " + (endTime-startTime) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Insert test fail!!");
        }
    }

    // 所有insert语句构成一个大事务场景测试
    @Test
    public void testSingleTxInsert() {
        log.info("testSingleTxInsert begin");

        try {
            OrderInsertDao insertDao = new OrderInsertDaoNoBatchSingleTxImpl("jdbc:mysql://192.168.3.144/test?useServerPrepStmts=true", "OrderBaseInfo", "root", "123456");
            Order[] orders = genTestOrders(1000000);

            long startTime = System.currentTimeMillis();
            insertDao.insertOrders(orders);
            long endTime = System.currentTimeMillis();

            log.info("testSingleTxInsert spend time " + (endTime-startTime) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Insert test fail!!");
        }
    }


    private void __testBatchInsert(int batchSize) {
        log.info("testBatchInsert " + batchSize + " begin");

        try {
            OrderInsertDao insertDao = new OrderInsertDaoBatchSingleTxImpl("jdbc:mysql://192.168.3.144/test?useServerPrepStmts=true", "OrderBaseInfo", "root", "123456", batchSize);
            Order[] orders = genTestOrders(1000000);

            long startTime = System.currentTimeMillis();
            insertDao.insertOrders(orders);
            long endTime = System.currentTimeMillis();

            log.info("testSingleTxInsert spend time " + (endTime-startTime) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Insert test fail!!");
        }
    }


    // 批量插入，每批5条insert场景测试
    @Test
    public void testBatchInsertBatchSize5() {
        __testBatchInsert(5);
    }

    // 批量插入，每批10条insert场景测试
    @Test
    public void testBatchInsertBatchSize10() {
        __testBatchInsert(10);
    }

    // 批量插入，每批10条insert场景测试
    @Test
    public void testBatchInsertBatchSize20() {
        __testBatchInsert(20);
    }

    // 批量插入，每批100条insert场景测试
    @Test
    public void testBatchInsertBatchSize100() {
        __testBatchInsert(100);
    }

    // 批量插入，每批500条insert场景测试
    @Test
    public void testBatchInsertBatchSize500() {
        __testBatchInsert(500);
    }

    // 批量插入，每批1000条insert场景测试
    @Test
    public void testBatchInsertBatchSize1000() {
        __testBatchInsert(1000);
    }

    // 批量插入，每批5000条insert场景测试
    @Test
    public void testBatchInsertBatchSize5000() {
        __testBatchInsert(5000);
    }
}
