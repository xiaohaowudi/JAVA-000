package com.javacamp.my_cache;

import com.javacamp.my_cache.annotation.MyCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class MyCacheApplication {

	AtomicInteger cnt = new AtomicInteger();

	public MyCacheApplication() {
		// 启动新线程，每秒修改一次内部状态，用于测试缓存效果
		new Thread( () -> {
			while (true) {
				cnt.incrementAndGet();
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} ).start();

	}

	@MyCache(5)
	public int getValCache5() {
		return cnt.get();
	}

	@MyCache(10)
	public int getValCache10() {
		return cnt.get();
	}

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-my-cache-conf.xml");

		MyCacheApplication app = (MyCacheApplication)context.getBean("myCacheApplication");

		System.err.println(Arrays.toString(context.getBeanDefinitionNames()));

		for (int i = 0; i < 1000; i++) {
			System.out.println("result of getValCache5 : "  + app.getValCache5());
			System.out.println("result of getValCache10 : "  + app.getValCache10());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
