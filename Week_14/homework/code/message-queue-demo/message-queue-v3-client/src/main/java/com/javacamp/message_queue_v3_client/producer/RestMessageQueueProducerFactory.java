package com.javacamp.message_queue_v3_client.producer;

import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_core_api.producer.ProducerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultHttpClientConnectionOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestMessageQueueProducerFactory implements ProducerFactory {

    @Value("${message_queue.host}")
    private String messageQueueHost;

    @Value("${message_queue.port}")
    private Integer messageQueuePort;

    @Override
    public Producer createProducer(String brokerId) {
        return new RestMessageQueueProducer(brokerId, messageQueueHost, messageQueuePort);
    }
}
