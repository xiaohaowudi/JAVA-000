package com.javacamp.message_queue_core_api.broker;

import com.javacamp.message_queue_core_api.message.Message;

// 支持随机位置读取消息的Broker接口
public interface RandomAccessBroker extends Broker {
    // 从指定位置startOffset开始消费消息
    Message pollMessage(String consumerId, String topic, Long startOffset);

    // 消息确认接口
    boolean confirmConsume(String consumerId, String topic, Long offset);
}
