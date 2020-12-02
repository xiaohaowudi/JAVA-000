package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadWriteDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;

@Service("insertOrderServiceInject")
public class InsertOrderServiceInjectImpl implements InsertOrderService {
    @Resource(name = "readWriteDataSource")
    ReadWriteDataSource dataSource;

    @Resource(name = "orderDao")
    OrderDao orderDao;

    @Override
    public boolean insertOrder(Order order) {
        boolean success = true;

        try {
            Connection conn = dataSource.getRwConnection();
            success = orderDao.insertOrder(conn, order);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }
}
