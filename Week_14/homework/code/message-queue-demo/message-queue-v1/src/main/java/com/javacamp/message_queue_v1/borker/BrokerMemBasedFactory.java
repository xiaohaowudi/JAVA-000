package com.javacamp.message_queue_v1.borker;

import com.javacamp.message_queue_core_api.broker.Broker;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;



@Component
public class BrokerMemBasedFactory {

    static ConcurrentHashMap<String, BrokerMemBasedImpl> id2broker = new ConcurrentHashMap<>();

    public Broker getBroker(String brokerId) {
        id2broker.putIfAbsent(brokerId, new BrokerMemBasedImpl());
        return id2broker.get(brokerId);
    }

}
