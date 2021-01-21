package com.grh.kafka_spring_demo;

import com.grh.kafka_spring_demo.producer.KafkaProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KafkaSpringDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaSpringDemoApplication.class, args);

        KafkaProducer producer = context.getBean(KafkaProducer.class);
        producer.sendMessage();
    }

}
