package com.javacamp.message_queue_v2.consumer;

import com.javacamp.message_queue_core_api.consumer.Consumer;
import com.javacamp.message_queue_core_api.consumer.ConsumerFactory;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumerFactory;
import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerFactoryV2 implements RandomAccessConsumerFactory {

    @Autowired
    RandomAccessBrokerFactory randomAccessBrokerFactory;

    @Override
    public RandomAccessConsumer createConsumer(String consumerId, String brokerId) {
        return new ConsumerV2(consumerId, randomAccessBrokerFactory.createBroker(brokerId));
    }
}
