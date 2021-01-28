package com.javacamp.message_queue_v2.consumer;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.consumer.Consumer;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.message.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConsumerV2 implements RandomAccessConsumer {
    private String consumerId;
    private RandomAccessBroker broker;

    @Override
    public Message recvMessageSync(String topic) {
        return broker.pollMessage(consumerId, topic);
    }

    @Override
    public Message recvMessageSync(String topic, Long startOffset) {
        return broker.pollMessage(consumerId, topic, startOffset);
    }

    @Override
    public boolean confirmConsume(String topic, Long offset) {
        return broker.confirmConsume(consumerId, topic, offset);
    }
}
