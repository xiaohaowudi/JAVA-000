package com.javacamp.message_queue_core_api.producer;

import com.javacamp.message_queue_core_api.message.Message;

public interface Producer {
    // 同步发送消息接口
    boolean sendMessageSync(String topic, Message message);

    // 同步发送接口，带消息TTL
    boolean sendMessageSync(String topic, Message message, Long ttl);
}
