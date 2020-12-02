package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;

public interface ReadOrderService {

    Order getOrderById(long orderId);
}
