package com.javacamp.message_queue_core_api.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Message {
    private Map<String, Object> headers = new HashMap<>();
    private byte[] body;
    private boolean valid;        // 是否是有效消息

    public Message(byte[] body) {
        this.body = body;
        this.valid = true;
    }

    public void setTtl(Long ttl) {
        headers.put("ttl", ttl);
    }

    public Long getTtl() {
        return (Long)headers.getOrDefault("ttl", 0x7fffffffL);
    }

    public Long getOffset() throws Exception {
        return (Long)headers.get("offset");
    }

    public void setOffset(Long offset) {
        headers.put("offset", offset);
    }
}
