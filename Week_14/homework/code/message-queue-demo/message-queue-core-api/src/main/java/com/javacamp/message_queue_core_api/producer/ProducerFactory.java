package com.javacamp.message_queue_core_api.producer;

// 生产者工厂类
public interface ProducerFactory {
    // 创建生产者方法
    Producer createProducer(String brokerId);
}
