package com.javacamp.message_queue_core_api.broker;

import com.javacamp.message_queue_core_api.message.Message;

public interface Broker {
    // 向固定topic发布消息接口
    boolean publishMessage(String topic, Message message);

    // 从固定topic收取消息接口
    Message pollMessage(String consumerId, String topic);

    // 创建topic接口
    boolean createTopic(String topic);

    // 删除topic接口
    boolean deleteTopic(String topic);
}
