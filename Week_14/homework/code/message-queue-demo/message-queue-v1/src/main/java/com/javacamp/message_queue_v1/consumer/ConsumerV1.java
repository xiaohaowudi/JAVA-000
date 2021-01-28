package com.javacamp.message_queue_v1.consumer;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.consumer.Consumer;
import com.javacamp.message_queue_core_api.message.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConsumerV1 implements Consumer {
    private final Broker broker;

    @Override
    public Message recvMessageSync(String topic) {
        return broker.pollMessage("", topic);
    }
}
