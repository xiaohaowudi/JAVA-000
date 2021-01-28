package com.javacamp.message_queue_v3_client.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class RestMessageQueueConsumerFactory implements RandomAccessConsumerFactory {

    @Value("${message_queue.host}")
    private String messageQueueHost;

    @Value("${message_queue.port}")
    private Integer messageQueuePort;

    @Override
    public RandomAccessConsumer createConsumer(String consumerId, String brokerId) {
        return new RestMessageQueueConsumer(brokerId, consumerId, messageQueueHost, messageQueuePort);
    }
}
