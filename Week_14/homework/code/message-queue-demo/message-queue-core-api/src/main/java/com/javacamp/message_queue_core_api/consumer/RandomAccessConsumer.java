package com.javacamp.message_queue_core_api.consumer;

import com.javacamp.message_queue_core_api.message.Message;

public interface RandomAccessConsumer extends Consumer {
    // 同步接收消息接口
    Message recvMessageSync(String topic, Long startOffset);

    boolean confirmConsume(String topic, Long offset);
}
