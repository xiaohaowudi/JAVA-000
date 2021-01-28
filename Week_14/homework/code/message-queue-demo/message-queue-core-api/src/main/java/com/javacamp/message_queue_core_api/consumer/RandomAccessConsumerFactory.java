package com.javacamp.message_queue_core_api.consumer;

public interface RandomAccessConsumerFactory {

    // 创建消费者接口
    RandomAccessConsumer createConsumer(String consumerId, String brokerId);
}
