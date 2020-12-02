package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;


@Service("insertOrderServiceAop")
public class InsertOrderServiceAopImpl implements InsertOrderService {

    @Resource(name = "orderDaoAopAutoBl")
    OrderDao orderDao;

    @Override
    public boolean insertOrder(Order order) {
        boolean success = true;
        try {
            success =  orderDao.insertOrder(null, order);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }
}
