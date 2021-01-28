package com.javacamp.message_queue_v3_client.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacamp.message_queue_core_api.consumer.RandomAccessConsumer;
import com.javacamp.message_queue_core_api.message.Message;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultHttpClientConnectionOperator;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@Slf4j

public class RestMessageQueueConsumer implements RandomAccessConsumer {
    
    private ObjectMapper objMapper;
    private HttpClient client;
    private String brokerId;
    private String consumerId;
    private String messageQueueHost;
    private Integer messageQueuePort;
    private final Base64.Decoder decoder = Base64.getDecoder();

    public RestMessageQueueConsumer(String brokerId, String consumerId, String messageQueueHost, Integer messageQueuePort) {
        this.brokerId = brokerId;
        this.consumerId = consumerId;
        this.messageQueueHost = messageQueueHost;
        this.messageQueuePort = messageQueuePort;
        objMapper = new ObjectMapper();
        client = new DefaultHttpClient();
    }

    // 执行返回值为boolean类型的请求
    private boolean execBooleanReturnReq(HttpUriRequest req) {
        try {
            HttpResponse rsp = null;
            rsp = client.execute(req);

            if (rsp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return false;
            }

            String rspStr = EntityUtils.toString(rsp.getEntity());
            log.info("get response : {}", rspStr);

            return rspStr.equals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 执行返回值为message类型的请求
    private Message execMessageReturnReq(HttpUriRequest req) {
        Message failRet = new Message(null);
        failRet.setValid(false);

        try {
            HttpResponse rsp = null;
            rsp = client.execute(req);

            if (rsp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return failRet;
            }

            String rspStr = EntityUtils.toString(rsp.getEntity());
            HashMap<String, Object> rspMap = (HashMap<String, Object>) objMapper.readValue(rspStr, HashMap.class);
            log.info("get response: {}", rspMap);

            if ((boolean)(rspMap.get("valid"))) {
                Message ret = new Message(decoder.decode( (String)(rspMap.get("body")) ));
                ret.setOffset(Long.parseLong(rspMap.get("offset").toString()));
                return ret;
            }

            return failRet;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return failRet;
    }

    @Override
    public Message recvMessageSync(String topic) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/message?";
        uri += "consumer=" + consumerId;
        uri += "&broker=" + brokerId;
        uri += "&topic=" + topic;
        uri += "&startOffset=-1";

        return execMessageReturnReq(new HttpGet(uri));
    }

    @Override
    public Message recvMessageSync(String topic, Long startOffset) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/message?";
        uri += "consumer=" + consumerId;
        uri += "&broker=" + brokerId;
        uri += "&topic=" + topic;
        uri += "&startOffset=" + startOffset;

        return execMessageReturnReq(new HttpGet(uri));
    }

    @Override
    public boolean confirmConsume(String topic, Long offset) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/consumer/offset?";
        uri += "consumer=" + consumerId;
        uri += "&broker=" + brokerId;
        uri += "&topic=" + topic;
        uri += "&offset=" + offset;

        return execBooleanReturnReq(new HttpPost(uri));
    }
}
