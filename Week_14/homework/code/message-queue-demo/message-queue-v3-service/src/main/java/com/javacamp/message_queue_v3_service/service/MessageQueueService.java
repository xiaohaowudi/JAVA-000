package com.javacamp.message_queue_v3_service.service;


import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import com.javacamp.message_queue_v2.consumer.ConsumerFactoryV2;
import com.javacamp.message_queue_v2.producer.ProducerFactoryV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageQueueService {
    @Autowired
    RandomAccessBrokerFactory brokerFactory;

    @Autowired
    ProducerFactoryV2 producerFactory;

    @Autowired
    ConsumerFactoryV2 consumerFactory;

    // 创建broker
    public boolean createBroker(String brokerId) {
        try {
            brokerFactory.createBroker(brokerId);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    // 删除broker
    public boolean deleteBroker(String brokerId) {
        try {
            return brokerFactory.deleteBroker(brokerId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    // 创建topic
    public boolean createTopic(String brokerId, String topic) {
        // 先阻塞住工厂删除broker的行为，避免并发时候发生错误
        brokerFactory.pauseFactoryDeleteOper();

        try {
            RandomAccessBroker broker = brokerFactory.createBroker(brokerId);
            return broker.createTopic(topic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            brokerFactory.resumeFactoryDeleteOper();
        }

        return false;
    }

    // 删除topic
    public boolean deleteTopic(String brokerId, String topic) {
        // 先阻塞住工厂删除broker的行为，避免并发时候发生错误
        brokerFactory.pauseFactoryDeleteOper();

        try {
            RandomAccessBroker broker = brokerFactory.createBroker(brokerId);
            return broker.deleteTopic(topic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            brokerFactory.resumeFactoryDeleteOper();
        }

        return false;
    }

    // 增加消息
    public boolean appendMessage(String brokerId, String topic, Message message) {
        // 先阻塞住工厂删除broker的行为，避免并发时候发生错误
        brokerFactory.pauseFactoryDeleteOper();

        try {
            RandomAccessBroker broker = brokerFactory.createBroker(brokerId);
            return broker.publishMessage(topic, message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            brokerFactory.resumeFactoryDeleteOper();
        }

        return false;
    }

    // 消费消息
    public Message pollMessage(String consumerId, String brokerId, String topic, Long startOffset) {
        // 先阻塞住工厂删除broker的行为，避免并发时候发生错误
        brokerFactory.pauseFactoryDeleteOper();
        Message msg = null;

        try {
            RandomAccessBroker broker = brokerFactory.createBroker(brokerId);
            if (startOffset != null) {
                msg = broker.pollMessage(consumerId, topic, startOffset);
            } else {
                msg = broker.pollMessage(consumerId, topic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            brokerFactory.resumeFactoryDeleteOper();
        }

        if (msg == null) {
            msg = new Message(null);
            msg.setValid(false);
        }

        return msg;
    }

    // 提交消费偏移量
    public Boolean confirmConsumeOffset(String consumerId, String brokerId, String topic, Long offset) {
        brokerFactory.pauseFactoryDeleteOper();
        try {
            RandomAccessBroker broker = brokerFactory.createBroker(brokerId);
            return broker.confirmConsume(consumerId, topic, offset);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            brokerFactory.resumeFactoryDeleteOper();
        }

        return false;
    }


}
