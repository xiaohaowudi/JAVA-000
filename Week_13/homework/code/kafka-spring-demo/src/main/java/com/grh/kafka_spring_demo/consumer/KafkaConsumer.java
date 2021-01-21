package com.grh.kafka_spring_demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// 最简单的kafka consumer 接收数据并进行打印
@Component
public class KafkaConsumer {
    @KafkaListener(topics = {"grh-topic"})
    public void onMessageRecv(ConsumerRecord<?,?> record) {
        System.err.printf("%s : %d : %d : %s : %s\n",
                Thread.currentThread().getName(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value());
    }
}
