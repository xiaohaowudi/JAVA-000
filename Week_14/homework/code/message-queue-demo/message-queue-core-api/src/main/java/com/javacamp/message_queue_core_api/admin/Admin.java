package com.javacamp.message_queue_core_api.admin;

// 消息队列管理器接口
public interface Admin {

    // 增加broker接口
    boolean addBroker(String brokerId);

    // 删除broker接口
    boolean deleteBroker(String brokerId);

    // 增加topic接口
    boolean addTopic(String brokerId, String topic);

    // 删除topic接口
    boolean deleteTopic(String brokerId, String topic);
}
