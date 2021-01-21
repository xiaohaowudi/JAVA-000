package com.grh.kafka_spring_demo;

import com.grh.kafka_spring_demo.consumer.KafkaConsumer;
import com.grh.kafka_spring_demo.producer.KafkaProducer;
import com.grh.kafka_spring_demo.producer.KafkaProducerWithCallback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaSpringDemoApplicationTests {
    @Autowired
    KafkaProducer producer;

    @Autowired
    KafkaProducerWithCallback producerWithCallback;


    @Autowired
    KafkaConsumer consumer;


    @Test
    void testSendDataSimple() {
        producer.sendMessage();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testSendDataWithCallback() {
        producerWithCallback.sendMessage();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
