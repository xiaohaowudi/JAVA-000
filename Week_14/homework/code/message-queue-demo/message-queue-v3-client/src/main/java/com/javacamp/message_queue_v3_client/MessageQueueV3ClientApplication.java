package com.javacamp.message_queue_v3_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.javacamp"})
public class MessageQueueV3ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageQueueV3ClientApplication.class, args);
    }

}
