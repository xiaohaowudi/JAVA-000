package com.javacamp.message_queue_v2.producer;

import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_core_api.producer.ProducerFactory;
import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class ProducerFactoryV2 implements ProducerFactory {
    @Autowired
    @Qualifier("randomAccessBrokerFactory")
    RandomAccessBrokerFactory brokerFactory;

    @Override
    public Producer createProducer(String brokerId) {
        return new ProducerV2(brokerFactory.createBroker(brokerId));
    }
}
