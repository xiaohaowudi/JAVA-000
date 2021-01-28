package com.javacamp.message_queue_v2.broker;

import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_v2.schd.TimerPollable;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class RandomAccessQueue implements TimerPollable {
    private final Map<Long, Message> offset2message = new HashMap<>();
    private final TreeSet<Long> validOffset = new TreeSet<>();
    private final Map<String, Long> consumerId2offset = new HashMap<>();
    private Long tailOffset = -1L;
    private String queueId;

    public RandomAccessQueue(String queueId) {
        this.queueId = queueId;
    }

    @Override
    public void pollWithTimer(Long periodMilliSec) {
        synchronized (this) {
            List<Long> delOffsets = new LinkedList<>();
            offset2message.forEach((key, value) -> {
                Long ttl = value.getTtl();
                if (ttl <= 0) {
                    delOffsets.add(key);
                } else {
                    value.getHeaders().put("ttl", ttl - periodMilliSec);
                }
            });

            delOffsets.forEach((offset) -> {
                    offset2message.remove(offset);
                    validOffset.remove(offset);
                    log.info("delete message, queue = {}, offset = {}", this.queueId, offset);
                }
            );
        }
    }

    // 获取消息
    public Message poolMessage(String consumerId, Long startOffset) {
        synchronized (this) {
            if (!consumerId2offset.containsKey(consumerId)) {
                consumerId2offset.put(consumerId, -1L);
            }

            Long nextOffset = null;
            if (startOffset == null) {
                nextOffset = validOffset.higher(consumerId2offset.get(consumerId));
            } else {
                nextOffset = validOffset.higher(startOffset - 1);
            }

            if (nextOffset == null) {
                return null;
            }

            Message message = offset2message.get(nextOffset);
            message.setOffset(nextOffset);

            return message;
        }
    }

    public Message poolMessage(String consumerId) {
        return poolMessage(consumerId, null);
    }

    // 存储消息
    public boolean appendMessage(Message message) {
        message.getHeaders().putIfAbsent("ttl", 0x7fffffffL);

        synchronized (this) {
            tailOffset += 1;
            message.setOffset(tailOffset);
            offset2message.put(tailOffset, message);
            validOffset.add(tailOffset);
        }

        log.info("append message success, queue = {}, body = {}, offset = {}", this.queueId, new String(message.getBody()), tailOffset);
        return true;
    }

    public boolean confirmOffset(String consumerId, Long offset) {
        synchronized (this) {
            if (!consumerId2offset.containsKey(consumerId)) {
                return false;
            }

            consumerId2offset.put(consumerId, offset);
            return true;
        }
    }


}
