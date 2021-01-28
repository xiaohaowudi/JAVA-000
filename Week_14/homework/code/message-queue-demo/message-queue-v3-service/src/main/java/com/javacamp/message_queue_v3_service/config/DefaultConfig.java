package com.javacamp.message_queue_v3_service.config;


import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfig {
    @Autowired
    RandomAccessBrokerFactory brokerFactory;

    // 创建默认的进行测试用的Broker和Topic
    @Autowired
    public void defaultInit() {
        brokerFactory.createBroker("testBroker").createTopic("testTopic");
    }

}
