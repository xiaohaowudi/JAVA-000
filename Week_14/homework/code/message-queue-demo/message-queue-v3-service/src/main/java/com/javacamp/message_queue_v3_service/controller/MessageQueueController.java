package com.javacamp.message_queue_v3_service.controller;


import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_v2.broker.RandomAccessBrokerFactory;
import com.javacamp.message_queue_v3_service.service.MessageQueueService;
import com.sun.scenario.effect.Offset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class MessageQueueController {

    @Autowired
    RandomAccessBrokerFactory brokerFactory;

    @Autowired
    MessageQueueService messageQueueService;

    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();

    // 投递消息接口
    @PutMapping ("/message")
    Boolean publishMessage(@RequestParam("broker") String brokerId,
                           @RequestParam("topic") String topic,
                           @RequestParam("ttl") Long ttl,
                           @RequestBody String messageContent) {
        log.info("controller get message : broker:{} topic:{} ttl:{} message:{}", brokerId, topic, ttl, messageContent);

        Message message = new Message(decoder.decode(messageContent));
        message.setTtl(ttl);
        return messageQueueService.appendMessage(brokerId, topic, message);
    }

    // 获取消息接口
    @GetMapping("/message")
    Map<String, Object> pollMessage(
            @RequestParam("consumer") String consumerId,
            @RequestParam("broker") String brokerId,
            @RequestParam("topic") String topic,
            @RequestParam("startOffset") Long startOffset) throws Exception {

        log.info("controller get message : consumer:{} broker:{} topic:{} startOffset:{}", consumerId, brokerId, topic, startOffset);

        Map<String, Object> ret = new HashMap<>();

        Long offset = startOffset >= 0 ? startOffset : null;
        Message message = messageQueueService.pollMessage(consumerId, brokerId, topic, offset);
        if (!message.isValid()) {
            ret.put("offset", null);
            ret.put("valid", false);
            ret.put("body", null);
        } else {
            ret.put("offset", message.getOffset());
            ret.put("valid", message.isValid());
            ret.put("body", encoder.encodeToString(message.getBody()));
        }
        return ret;
    }

    // 提交消费接口
    @PostMapping("/consumer/offset")
    Boolean confirmConsumeOffset (
        @RequestParam("consumer") String consumerId,
        @RequestParam("broker") String brokerId,
        @RequestParam("topic") String topic,
        @RequestParam("offset") Long offset) {

        log.info("controller get message : consumer:{} broker:{} topic:{} offset:{}", consumerId, brokerId, topic, offset);
        return messageQueueService.confirmConsumeOffset(consumerId, brokerId, topic, offset);
    }

    // 添加broker
    @PutMapping("/broker")
    Boolean createBroker(@RequestParam("broker") String brokerId) {
        log.info("controller get message : brokerId : {}", brokerId);
        return messageQueueService.createBroker(brokerId);
    }

    // 删除broker
    @DeleteMapping("/broker")
    Boolean deleteBroker(@RequestParam("broker") String brokerId) {
        log.info("controller get message : brokerId : {}", brokerId);
        return messageQueueService.deleteBroker(brokerId);
    }

    // 添加topic
    @PutMapping("/topic")
    Boolean createTopic(@RequestParam("broker") String brokerId,
                        @RequestParam("topic") String topic) {
        log.info("controller get message : brokerId : {}, topic : {}", brokerId, topic);
        return messageQueueService.createTopic(brokerId, topic);
    }

    // 删除topic
    @DeleteMapping("/topic")
    Boolean deleteTopic(@RequestParam("broker") String brokerId,
                        @RequestParam("topic") String topic) {

        log.info("controller get message : brokerId : {}, topic : {}", brokerId, topic);
        return messageQueueService.deleteTopic(brokerId, topic);
    }
}
