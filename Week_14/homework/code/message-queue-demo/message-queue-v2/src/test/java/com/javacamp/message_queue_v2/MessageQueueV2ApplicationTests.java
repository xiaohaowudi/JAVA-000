package com.javacamp.message_queue_v2;

import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import com.javacamp.message_queue_v2.consumer.ConsumerFactoryV2;
import com.javacamp.message_queue_v2.producer.ProducerFactoryV2;
import com.javacamp.message_queue_v2.producer.ProducerV2;
import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
class MessageQueueV2ApplicationTests {

    @Autowired
    RandomAccessBrokerFactory brokerFactory;

    @Autowired
    ProducerFactoryV2 producerFactory;

    @Autowired
    ConsumerFactoryV2 consumerFactory;

    @Test
    void testNoConfirmGetMessage() {
        RandomAccessBroker broker = brokerFactory.createBroker("testBroker");
        broker.createTopic("testTopic");

        Producer producer = producerFactory.createProducer("testBroker");

        final CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            final int curIdx = i;
            new Thread(
                () -> {
                    for (int ii = 0; ii < 10; ii++) {
                        Message message = new Message(("hello world  " + (curIdx*10 + ii)).getBytes(StandardCharsets.UTF_8));
                        producer.sendMessageSync("testTopic", message, 1000L);
                    }
                    latch.countDown();
                }
            ).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final CountDownLatch consumerCountDownLatch = new CountDownLatch(3);

        // 三个任务读取数据时候都不进行提交，都只会消耗同一条消息
        for (int i = 0; i < 3; i++) {
            final int curIdx = i;
            new Thread(
                () -> {
                    RandomAccessConsumer consumer = consumerFactory.createConsumer("consumer" + curIdx, "testBroker");
                    try {
                        for (int ii = 0; ii < 10; ii++) {
                            Message message = consumer.recvMessageSync("testTopic", (long)(curIdx * 2));
                            if (message != null) {
                                log.info("get message, body = {}, offset = {}", new String(message.getBody()), message.getOffset());
                            } else {
                                log.info("get message fail");
                            }
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage(), e);
                    } finally {
                        consumerCountDownLatch.countDown();
                    }
                }
            ).start();
        }

        try {
            consumerCountDownLatch.await();

            // 等待2s, 让消息删除
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testConfirmGetMessage() {
        RandomAccessBroker broker = brokerFactory.createBroker("testBroker");
        broker.createTopic("testTopic");

        Producer producer = producerFactory.createProducer("testBroker");

        final CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            final int curIdx = i;
            new Thread(
                    () -> {
                        for (int ii = 0; ii < 10; ii++) {
                            Message message = new Message(("hello world  " + (curIdx*10 + ii)).getBytes(StandardCharsets.UTF_8));
                            producer.sendMessageSync("testTopic", message, 1000L);
                        }
                        latch.countDown();
                    }
            ).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final CountDownLatch consumerCountDownLatch = new CountDownLatch(3);

        // 三个任务读取数据时候都进行提交，3个线程能够把30条消息消费完
        for (int i = 0; i < 3; i++) {
            final int curIdx = i;
            new Thread(
                    () -> {
                        RandomAccessConsumer consumer = consumerFactory.createConsumer("consumer" + curIdx, "testBroker");
                        try {
                            for (int ii = 0; ii < 10; ii++) {
                                Message message = null;
                                if (ii == 0) {
                                    message = consumer.recvMessageSync("testTopic", (long) (curIdx * 10));
                                } else {
                                    message = consumer.recvMessageSync("testTopic");
                                }

                                if (message != null) {
                                    log.info("get message, body = {}, offset = {}", new String(message.getBody()), message.getOffset());

                                    // 提交消息消费成功
                                    consumer.confirmConsume("testTopic", message.getOffset());

                                } else {
                                    log.info("get message fail");
                                }
                            }
                        } catch (Exception e) {
                            log.info(e.getMessage(), e);
                        } finally {
                            consumerCountDownLatch.countDown();
                        }
                    }
            ).start();
        }

        try {
            consumerCountDownLatch.await();

            // 等待2s, 让消息删除
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
