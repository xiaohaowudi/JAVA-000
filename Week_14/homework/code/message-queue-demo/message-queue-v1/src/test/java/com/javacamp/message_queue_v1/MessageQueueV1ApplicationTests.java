package com.javacamp.message_queue_v1;

import com.javacamp.message_queue_core_api.broker.Broker;
import com.javacamp.message_queue_core_api.consumer.Consumer;
import com.javacamp.message_queue_core_api.consumer.ConsumerFactory;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_core_api.producer.ProducerFactory;
import com.javacamp.message_queue_v1.borker.BrokerMemBasedFactory;
import com.javacamp.message_queue_v1.consumer.ConsumerFactoryV1;
import com.javacamp.message_queue_v1.producer.ProducerFactoryV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
class MessageQueueV1ApplicationTests {

    @Autowired
    private BrokerMemBasedFactory brokerMemBasedFactory;

    @Autowired
    @Qualifier("consumerFactoryV1")
    private ConsumerFactory consumerFactory;

    @Autowired
    @Qualifier("producerFactoryV1")
    private ProducerFactory producerFactory;


    @Test
    void testPublishAndGetMessage() {
        Broker broker = brokerMemBasedFactory.getBroker("test");
        broker.createTopic("testTopic");
        CountDownLatch countDownLatch = new CountDownLatch(3);

        List<Producer> producers = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            producers.add(producerFactory.createProducer("test"));
        }

        producers.forEach(
            (producer) -> {
                new Thread( () -> {
                    for (int ii = 0; ii < 10; ii++) {
                        String str = "hello world" + ii + ":" + Thread.currentThread().getName();
                        producer.sendMessageSync("testTopic", new Message(str.getBytes(StandardCharsets.UTF_8)));
                    }

                    countDownLatch.countDown();
                } ).start();
            }
        );

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 接收消息
        List<Consumer> consumers = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            consumers.add(consumerFactory.createConsumer("consumer-" + i, "test"));
        }

        CountDownLatch consumerCountDownLatch = new CountDownLatch(3);
        consumers.forEach(

            (consumer) -> {
                new Thread (
                    () -> {
                        for (int ii = 0; ii < 10; ii++) {
                            Message message = consumer.recvMessageSync("testTopic");
                            log.info("get message : {} ", new String(message.getBody()));
                        }
                        consumerCountDownLatch.countDown();
                    }
                ).start();
            }
        );

        try {
            consumerCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
