package io.kimmking.rpcfx.demo.consumer;

import io.kimmking.rpcfx.client.Rpcfx;
import io.kimmking.rpcfx.demo.api.Order;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"client_stub", "io.kimmking.rpcfx.client"})
public class RpcfxClientApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(RpcfxClientApplication.class, args);
		Rpcfx rpcfx = context.getBean(Rpcfx.class);

		UserService userService = rpcfx.create(UserService.class);
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());

		OrderService orderService = rpcfx.create(OrderService.class);
		Order order = orderService.findOrderById(1992129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
	}
}
