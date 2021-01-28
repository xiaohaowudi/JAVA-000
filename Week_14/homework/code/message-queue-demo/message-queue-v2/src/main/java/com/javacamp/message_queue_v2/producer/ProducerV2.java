package com.javacamp.message_queue_v2.producer;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProducerV2 implements Producer {
    private Broker broker;

    @Override
    public boolean sendMessageSync(String topic, Message message) {
        return sendMessageSync(topic, message, 0x7fffffffL);
    }

    @Override
    public boolean sendMessageSync(String topic, Message message, Long ttl) {
        message.setTtl(ttl);
        return broker.publishMessage(topic, message);
    }
}
