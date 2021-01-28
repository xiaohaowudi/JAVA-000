package com.javacamp.message_queue_v1.producer;

import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_core_api.broker.Broker;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProducerV1 implements Producer {
    private final Broker broker;

    @Override
    public boolean sendMessageSync(String topic, Message message) {
        return broker.publishMessage(topic, message);
    }

    @Override
    public boolean sendMessageSync(String topic, Message message, Long ttl) {
        // v1 版本不支持ttl, 直接调用重载版本
        return sendMessageSync(topic, message);
    }
}
