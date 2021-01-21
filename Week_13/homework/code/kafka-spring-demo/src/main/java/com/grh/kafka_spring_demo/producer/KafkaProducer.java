package com.grh.kafka_spring_demo.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// 最简单的kafka producer
@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage() {
        List<ListenableFuture<SendResult<String, Object>>> futures = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            ListenableFuture<SendResult<String, Object>> f = kafkaTemplate.send("grh-topic", "key-"+i, "value-"+i);
            futures.add(f);
        }

        futures.forEach((f) -> {
            try {
                System.err.println(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
