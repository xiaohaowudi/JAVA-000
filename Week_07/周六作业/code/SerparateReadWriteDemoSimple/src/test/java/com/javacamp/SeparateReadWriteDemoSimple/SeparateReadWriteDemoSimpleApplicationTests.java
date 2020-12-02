package com.javacamp.SeparateReadWriteDemoSimple;

import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import com.javacamp.SeparateReadWriteDemoSimple.Service.InsertOrderService;
import com.javacamp.SeparateReadWriteDemoSimple.Service.ReadOrderService;
import com.javacamp.SeparateReadWriteDemoSimple.Service.RebuildOrderTableService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@Log
@SpringBootApplication
class SeparateReadWriteDemoSimpleApplicationTests {


	private Order[] genTestOrders(int orderNum) {
		if (orderNum <= 0) {
			throw new RuntimeException("parameter error");
		}

		Order[] orders = new Order[orderNum];
		for (int i = 0; i < orderNum; i++) {
			orders[i] = new Order(0, i+100, i, (byte) (i%2), "123456789###"+ i, System.currentTimeMillis(), System.currentTimeMillis());
		}
		return orders;
	}

	private void printBeanNames(ConfigurableApplicationContext context) {
		String[] names = context.getBeanDefinitionNames();

		for (String name : names) {
			log.info("bean name " + name);
		}

	}

	// 测试注入方式实现的读写分离
	@Test
	public void testOrderInsertAndReadInject() {
		ConfigurableApplicationContext context = SpringApplication.run(SeparateReadWriteDemoSimpleApplicationTests.class);
		printBeanNames(context);
		RebuildOrderTableService rebuildService = context.getBean("rebuildOrderTableService", RebuildOrderTableService.class);
		rebuildService.rebuildOrderTable();



		InsertOrderService insertService = context.getBean("insertOrderServiceInject", InsertOrderService.class);

		long startTime = System.currentTimeMillis();
		Order[] orders = genTestOrders(1000);
		for (Order order : orders) {
			insertService.insertOrder(order);
		}

		ReadOrderService readService = context.getBean("readOrderServiceInject", ReadOrderService.class);
		for (int i = 1; i <= 1000; i++) {
			log.info(readService.getOrderById(i).toString());
		}

		log.info("spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}



	// 测试AOP实现的自动选择数据源且负载均衡的读写分离
	@Test
	public void testOrderInsertAndReadAop() {
		ConfigurableApplicationContext context = SpringApplication.run(SeparateReadWriteDemoSimpleApplicationTests.class);
		RebuildOrderTableService rebuildService = context.getBean("rebuildOrderTableService", RebuildOrderTableService.class);
		rebuildService.rebuildOrderTable();

		InsertOrderService insertService = context.getBean("insertOrderServiceAop", InsertOrderService.class);

		long startTime = System.currentTimeMillis();
		Order[] orders = genTestOrders(1000);
		for (Order order : orders) {
			insertService.insertOrder(order);
		}

		ReadOrderService readService = context.getBean("readOrderServiceAop", ReadOrderService.class);
		for (int i = 1; i <= 1000; i++) {
			log.info(readService.getOrderById(i).toString());
		}

		log.info("spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

}
