package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadOnlyDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;

// 用注入方式实现的读取order的Service
@Service("readOrderServiceInject")
public class ReadOrderServiceInjectImpl implements  ReadOrderService {

    @Resource(name = "readOnlyDataSource1")
    ReadOnlyDataSource dataSource;

    @Resource(name = "orderDao")
    OrderDao orderDao;

    @Override
    public Order getOrderById(long orderId) {
        Order order = null;
        try {
            Connection conn = dataSource.getRoConnection();
            order = orderDao.getOrderById(conn, orderId);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
}
