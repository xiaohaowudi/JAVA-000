package com.javacamp.SeparateReadWriteDemoSimple;

import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import com.javacamp.SeparateReadWriteDemoSimple.Service.InsertOrderService;
import com.javacamp.SeparateReadWriteDemoSimple.Service.ReadOrderService;
import com.javacamp.SeparateReadWriteDemoSimple.Service.RebuildOrderTableService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SeparateReadWriteDemoSimpleApplication {


	private static Order[] genTestOrders(int orderNum) {
		if (orderNum <= 0) {
			throw new RuntimeException("parameter error");
		}

		Order[] orders = new Order[orderNum];
		for (int i = 0; i < orderNum; i++) {
			orders[i] = new Order(0, i+100, i, (byte) (i%2), "123456789###"+ i, System.currentTimeMillis(), System.currentTimeMillis());
		}
		return orders;
	}

	public static void main(String[] args) {
		//ConfigurableApplicationContext context = SpringApplication.run(SeparateReadWriteDemoSimpleApplication.class, args);

		ConfigurableApplicationContext context = SpringApplication.run(SeparateReadWriteDemoSimpleApplication.class);
		RebuildOrderTableService rebuildService = context.getBean("rebuildOrderTableService", RebuildOrderTableService.class);
		rebuildService.rebuildOrderTable();

		InsertOrderService insertService = context.getBean("insertOrderServiceAop", InsertOrderService.class);

		Order[] orders = genTestOrders(5);
		for (Order order : orders) {
			insertService.insertOrder(order);
		}

		ReadOrderService readService = context.getBean("readOrderServiceAop", ReadOrderService.class);


	}

}
