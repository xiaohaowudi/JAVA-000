package com.javacmap.shardingjdbcdemo;

import com.javacmap.shardingjdbcdemo.dao.OrderBaseInfoMapper;
import com.javacmap.shardingjdbcdemo.model.OrderBaseInfo;
import com.javacmap.shardingjdbcdemo.model.OrderBaseInfoExample;
import lombok.extern.java.Log;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Log
class ShardingJdbcDemoApplicationTests {

    @Resource
    private OrderBaseInfoMapper orderBaseInfoMapper;

    // 测试插入1600条数据，可观察数据库中数据是不是在2个库，16张表中均匀分布
    @Test
    public void testInsertOrder() {
        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 160; customer_id++) {
                //order.setOrderId(order_id);
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }
    }

    // 测试从不同的库分片和表分片中查询160条数据，验证是否都能查询到结果
    @Test
    public void testQueryOrder() {

        int total_query_cnt = 0;
        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                OrderBaseInfoExample example = new OrderBaseInfoExample();
                example.createCriteria().andSupplierUserIdEqualTo(supplier_id).andCustomerUserIdEqualTo(customer_id);
                List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
                if (orders == null || orders.size() == 0) {
                    throw new RuntimeException("query fail!");
                }

                total_query_cnt += orders.size();

                for (OrderBaseInfo order : orders) {
                    log.info(order.toString());
                }
            }
        }

        log.info("total_query_cnt = " + total_query_cnt);
        if (total_query_cnt != 160) {
            throw new RuntimeException("total_query_cnt is not right, right answer is 160, but test result is " + total_query_cnt);
        }
    }

    // 测试修改记录再做查询, 先查询卖家是5，买家是8的所有订单信息，将订单中trade_no进行修改，然后再查询验证新的trade_no是否是修改之后的数值
    @Test
    public void testUpdateOrder() {
        OrderBaseInfoExample example = new OrderBaseInfoExample();
        example.createCriteria().andSupplierUserIdEqualTo(5L).andCustomerUserIdEqualTo(8L);

        List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
        for (OrderBaseInfo order : orders) {
            log.info(order.toString());

            // 限定查找记录的supplier_id和customer_id，只会触发一个库一个表的更新操作
            OrderBaseInfoExample updateExample = new OrderBaseInfoExample();
            updateExample.createCriteria()
                    .andSupplierUserIdEqualTo(order.getSupplierUserId())
                    .andCustomerUserIdEqualTo(order.getCustomerUserId())
                    .andOrderIdEqualTo(order.getOrderId());


            order.setTradeNo("this is a test trade_no");
            orderBaseInfoMapper.updateByExample(order, updateExample);
        }

        log.info("#############################################");

        // 重新读取记录
        orders = orderBaseInfoMapper.selectByExample(example);
        for (OrderBaseInfo order : orders) {
            log.info(order.toString());
            if (!order.getTradeNo().equals("this is a test trade_no")) {
                throw new RuntimeException("trade no is not right");
            }
        }
    }

    // 测试删除记录功能，将卖家是5，买家是8的所有订单信息删除掉，然后再次进行查询，验证记录数是否为0
    @Test
    public void testDeleteOrder() {
        OrderBaseInfoExample example = new OrderBaseInfoExample();
        example.createCriteria().andSupplierUserIdEqualTo(5L).andCustomerUserIdEqualTo(8L);

        List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
        log.info("before delete, record number is " + orders.size());

        orderBaseInfoMapper.deleteByExample(example);

        orders = orderBaseInfoMapper.selectByExample(example);
        log.info("after delete, record number is " + orders.size());
        if (orders.size() != 0) {
            throw new RuntimeException("record number should be 0, but record number is " + orders.size());
        }
    }


    // 删除所有数据
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value=false)  // 禁止JUnis自动回滚
    @ShardingTransactionType(TransactionType.XA)
    public void deleteAllData() {
        OrderBaseInfoExample example = new OrderBaseInfoExample();
        example.createCriteria().andOrderIdIsNotNull();
        orderBaseInfoMapper.deleteByExample(example);
    }


    // 对正常执行的XA事务进行测试, 插入160条数据，并且关闭junit自动回滚，可观察到到数据库完整插入160条数据
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value=false)  // 禁止JUnis自动回滚
    @ShardingTransactionType(TransactionType.XA)
    public void TestSuccessTrans() {

        deleteAllData();

        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                //order.setOrderId(order_id);
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }
    }


    // 测试XA分布式事务回滚, 插入160条数据，最后故意触发异常构造分布式事务回滚，可以观察到所有数据库表中均没有新数据插入，验证分布式事务完整回滚
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ShardingTransactionType(TransactionType.XA)
    public void TestFailTransRollback() {
        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }

        // 故意触发异常
        int a = 10 / 0;
    }
}
