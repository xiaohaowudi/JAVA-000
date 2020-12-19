package io.kimmking.dubbo.demo.consumer;

import io.kimmking.dubbo.demo.api.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DubboClientApplication {

	@DubboReference(version = "1.0.0")
	private Dollar2Rmb dollar2Rmb;

	public static void main(String[] args) {
		SpringApplication.run(DubboClientApplication.class).close();
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			// 用户A 1美元换7人民币，用户B 7人民币换1美元，共同构成分布式事务
			System.out.println(dollar2Rmb.transfer("userA", 1L, "userB", 7L));
		};
	}
}
