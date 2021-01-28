package com.javacamp.message_queue_v3_client;

import com.javacamp.message_queue_core_api.admin.Admin;
import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import com.javacamp.message_queue_v3_client.consumer.RestMessageQueueConsumer;
import com.javacamp.message_queue_v3_client.consumer.RestMessageQueueConsumerFactory;
import com.javacamp.message_queue_v3_client.producer.RestMessageQueueProducerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
class MessageQueueV3ClientApplicationTests {

    @Resource
    Admin admin;

    @Resource
    RestMessageQueueConsumerFactory consumerFactory;

    @Resource
    RestMessageQueueProducerFactory producerFactory;

    @Test
    void testAdminCreateBroker() {
        boolean ret = admin.addBroker("testBroker123");
        log.info("addBroker : {}", ret);
    }

    @Test
    void testAdminDeleteBroker() {
        boolean ret = admin.deleteBroker("testBroker123");
        log.info("deleteBroker : {}", ret);
    }

    @Test
    void testAdminCreateTopic() {
        boolean ret = admin.addTopic("testBroker123", "testTopic1234");
        log.info("addTopic : {}", ret);
    }

    @Test
    void testAdminDeleteTopic() {
        boolean ret = admin.deleteTopic("testBroker123", "testTopic1234");
        log.info("deleteTopic : {}", ret);
    }

    @Test
    void testConsumeMessageNoConfirm() {
        RandomAccessConsumer consumer = consumerFactory.createConsumer("consumer-1", "testBroker");

        for (int i = 0; i < 10; i++) {
            Message message = consumer.recvMessageSync("testTopic");
            log.info("consumer get message : {}", message);
        }
    }

    @Test
    void testProducer() {
        Producer producer = producerFactory.createProducer("testBroker");
        Message message = new Message("hello world".getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < 10; i++) {
            boolean ret = producer.sendMessageSync("testTopic", message);
            log.info("send message return : {}", ret);
        }
    }

    @Test
    void testConsumeMessageConfirm() throws Exception {
        RandomAccessConsumer consumer = consumerFactory.createConsumer("consumer-1", "testBroker");

        // 循环消费到topic中所有消息取完为止
        while (true) {
            Message message = consumer.recvMessageSync("testTopic");
            if (message.isValid()) {
                log.info("consumer get message, body:{} offset:{}", new String(message.getBody(), StandardCharsets.UTF_8), message.getOffset());
            } else {
                log.info("consume message fail");
                break;
            }

            boolean ret = consumer.confirmConsume("testTopic", message.getOffset());
            log.info("confirm : {}", ret);
        }
    }

    // 多线程场景测试
    @Test
    void testProducerAndConsumerMultiThread() {

        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(
                () -> {
                    Producer producer = producerFactory.createProducer("testBroker");
                    for (int ii = 0; ii < 10; ii++) {
                        Message message = new Message(("hello world").getBytes(StandardCharsets.UTF_8));
                        producer.sendMessageSync("testTopic", message, 600000L);
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

        final CountDownLatch consumerCountDownLatch = new CountDownLatch(10);

        // 10个任务读取数据时候都进行提交，10个线程能够把100条消息消费完
        for (int i = 0; i < 10; i++) {
            final int curIdx = i;
            new Thread(
                () -> {
                    RandomAccessConsumer consumer = consumerFactory.createConsumer("consumer-test-" + curIdx, "testBroker");
                    try {
                        while (true) {
                            Message message = null;
                            message = consumer.recvMessageSync("testTopic");

                            if (message != null && message.isValid()) {
                                log.info("get message, body = {}, offset = {}", new String(message.getBody(), StandardCharsets.UTF_8), message.getOffset());

                                // 提交消息消费成功
                                consumer.confirmConsume("testTopic", message.getOffset());

                            } else {
                                //log.info("get message fail");
                                break;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
