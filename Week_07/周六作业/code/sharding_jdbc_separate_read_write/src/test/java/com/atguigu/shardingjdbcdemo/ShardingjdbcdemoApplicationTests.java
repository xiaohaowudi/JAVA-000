package com.atguigu.shardingjdbcdemo;

import com.atguigu.shardingjdbcdemo.entity.OrderBase;
import com.atguigu.shardingjdbcdemo.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class ShardingjdbcdemoApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    // 插入1000条记录测试
    @Test
    public void insertOrder() {
        for (int i = 0; i < 1000; i++) {
            OrderBase order = new OrderBase();
            order.setSupplierUserId((long)i);
            order.setCustomerUserId((long)i);
            order.setStatus((byte) (i % 2 + 1));
            order.setTradeNo("abc123456789###" + i);
            order.setCreateTimeStamp(System.currentTimeMillis());
            order.setModifyTimeStamp(System.currentTimeMillis());
            orderMapper.insert(order);
        }
    }

    // 删除订单记录测试
    @Test
    public void deleteOrder() {
        QueryWrapper<OrderBase> wrapper = new QueryWrapper<OrderBase>();
        wrapper.eq("status", 1);
        orderMapper.delete(wrapper);
    }

    // 查询订单记录测试
    @Test
    public void queryOrder() {
        QueryWrapper<OrderBase> wrapper = new QueryWrapper<OrderBase>();
        wrapper.le("customer_user_id", 50);
        List<Object> orders = orderMapper.selectObjs(wrapper);

        for (Object order : orders) {
            System.out.println(order);
        }
    }

    // 修改订单记录测试
    @Test
    public void updateOrder() {
        QueryWrapper<OrderBase> wrapper = new QueryWrapper<OrderBase>();
        wrapper.eq("customer_user_id", 31);

        OrderBase order = orderMapper.selectOne(wrapper);
        System.out.println(order);

        order.setTradeNo("abcdefghijklmn");
        orderMapper.update(order, wrapper);

        OrderBase newOrder = orderMapper.selectOne(wrapper);
        System.out.println(newOrder);

        assertEquals(newOrder.getTradeNo(), "abcdefghijklmn");
    }
}
