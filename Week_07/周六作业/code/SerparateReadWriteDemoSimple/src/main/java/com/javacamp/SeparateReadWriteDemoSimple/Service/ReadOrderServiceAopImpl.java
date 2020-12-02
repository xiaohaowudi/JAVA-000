package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;

@Component("readOrderServiceAop")
public class ReadOrderServiceAopImpl implements ReadOrderService {

    @Resource(name = "orderDaoAopAutoBl")
    OrderDao orderDao;

    @Override
    public Order getOrderById(long orderId) {
        Order order = null;
        try {
            order = orderDao.getOrderById(null, orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
}
