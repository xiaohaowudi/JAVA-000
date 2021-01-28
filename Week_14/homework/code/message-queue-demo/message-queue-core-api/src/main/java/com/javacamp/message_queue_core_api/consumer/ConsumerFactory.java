package com.javacamp.message_queue_core_api.consumer;

public interface ConsumerFactory {
    // 创建消费者接口
    Consumer createConsumer(String consumerId, String brokerId);
}
