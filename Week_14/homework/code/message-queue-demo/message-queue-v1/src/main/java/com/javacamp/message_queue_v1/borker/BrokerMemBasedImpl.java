package com.javacamp.message_queue_v1.borker;


import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.broker.Broker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 基于内存Map实现的消息队列
public class BrokerMemBasedImpl implements Broker {
    private final Map<String, BlockingQueue<Message>> topic2queue = new HashMap<>();

    @Override
    public boolean createTopic(String topic) {
        topic2queue.putIfAbsent(topic, new LinkedBlockingQueue<Message>());
        return true;
    }

    @Override
    public boolean deleteTopic(String topic) {
        topic2queue.remove(topic);
        return true;
    }

    @Override
    public boolean publishMessage(String topic, Message message) {
        if (!topic2queue.containsKey(topic)) {
            return false;
        }

        return topic2queue.get(topic).offer(message);
    }

    @Override
    public Message pollMessage(String consumerId, String topic) {
        if (!topic2queue.containsKey(topic)) {
            return null;
        }

        return topic2queue.get(topic).poll();
    }
}
