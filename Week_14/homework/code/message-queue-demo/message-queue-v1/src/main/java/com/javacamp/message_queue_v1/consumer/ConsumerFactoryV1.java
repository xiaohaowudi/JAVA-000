package com.javacamp.message_queue_v1.consumer;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.consumer.Consumer;
import com.javacamp.message_queue_core_api.consumer.ConsumerFactory;
import com.javacamp.message_queue_v1.borker.BrokerMemBasedFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerFactoryV1 implements ConsumerFactory {

    @Autowired
    BrokerMemBasedFactory brokerFactory;

    @Override
    public Consumer createConsumer(String consumerId, String brokerId) {
        return new ConsumerV1(brokerFactory.getBroker(brokerId));
    }
}
