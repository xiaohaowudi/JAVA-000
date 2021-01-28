package com.javacamp.message_queue_v1.producer;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_core_api.producer.ProducerFactory;
import com.javacamp.message_queue_v1.borker.BrokerMemBasedFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerFactoryV1 implements ProducerFactory {

    @Autowired
    BrokerMemBasedFactory brokerMemBasedFactory;

    @Override
    public Producer createProducer(String brokerId) {
        return new ProducerV1(brokerMemBasedFactory.getBroker(brokerId));
    }
}
