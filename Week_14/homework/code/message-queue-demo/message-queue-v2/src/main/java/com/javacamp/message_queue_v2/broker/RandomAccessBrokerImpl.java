package com.javacamp.message_queue_v2.broker;

import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_v2.schd.TimerPollable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RandomAccessBrokerImpl implements RandomAccessBroker, TimerPollable {

    private HashMap<String, RandomAccessQueue> topic2queue = new HashMap<>();

    ReentrantLock deleteTopicLock = new ReentrantLock();

    @Override
    public synchronized boolean publishMessage(String topic, Message message) {
        if (!topic2queue.containsKey(topic)) {
            return false;
        }

        return topic2queue.get(topic).appendMessage(message);
    }

    @Override
    public synchronized Message pollMessage(String consumerId, String topic) {
        if (!topic2queue.containsKey(topic)) {
            return null;
        }

        return topic2queue.get(topic).poolMessage(consumerId);
    }

    @Override
    public synchronized Message pollMessage(String consumerId, String topic, Long startOffset) {
        if (!topic2queue.containsKey(topic)) {
            return null;
        }

        return topic2queue.get(topic).poolMessage(consumerId, startOffset);
    }

    @Override
    public boolean createTopic(String topic) {
        topic2queue.putIfAbsent(topic, new RandomAccessQueue("queue#" + topic));
        return true;
    }

    @Override
    public boolean deleteTopic(String topic) {
        deleteTopicLock.lock();

        try {
            if (!topic2queue.containsKey(topic)) {
                return false;
            }

            topic2queue.remove(topic);
            return true;
        } finally {
            deleteTopicLock.unlock();
        }
    }

    @Override
    public synchronized boolean confirmConsume(String consumerId, String topic, Long offset) {
        deleteTopicLock.lock();

        try {
            if (!topic2queue.containsKey(topic)) {
                return false;
            }

            return topic2queue.get(topic).confirmOffset(consumerId, offset);
        } finally {
            deleteTopicLock.unlock();
        }
    }

    @Override
    public synchronized void pollWithTimer(Long periodMilliSec) {
        topic2queue.values().forEach((queue) -> {
            queue.pollWithTimer(periodMilliSec);
        });
    }
}
