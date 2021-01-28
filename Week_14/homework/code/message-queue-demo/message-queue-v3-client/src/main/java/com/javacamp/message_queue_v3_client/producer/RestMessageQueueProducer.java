package com.javacamp.message_queue_v3_client.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacamp.message_queue_core_api.message.Message;
import com.javacamp.message_queue_core_api.producer.Producer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultHttpClientConnectionOperator;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Slf4j
public class RestMessageQueueProducer implements Producer {

    private final HttpClient client;
    private final String brokerId;
    private final String messageQueueHost;
    private final Integer messageQueuePort;
    private final Base64.Encoder encoder;

    public RestMessageQueueProducer(String brokerId, String messageQueueHost, Integer messageQueuePort) {
        this.brokerId = brokerId;
        this.messageQueueHost = messageQueueHost;
        this.messageQueuePort = messageQueuePort;
        client = new DefaultHttpClient();
        encoder = Base64.getEncoder();
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


    @Override
    public boolean sendMessageSync(String topic, Message message) {
        try {
            String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/message?";
            uri += "broker=" + brokerId;
            uri += "&topic=" + topic;
            uri += "&ttl=999999999999";

            HttpPut req = new HttpPut(uri);
            String charSet = "UTF-8";
            StringEntity entity = new StringEntity(encoder.encodeToString(message.getBody()));
            req.setEntity(entity);

            return execBooleanReturnReq(req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean sendMessageSync(String topic, Message message, Long ttl) {
        try {
            String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/message?";
            uri += "broker=" + brokerId;
            uri += "&topic=" + topic;
            uri += "&ttl=" + ttl;

            HttpPut req = new HttpPut(uri);
            String charSet = "UTF-8";
            StringEntity entity = new StringEntity(encoder.encodeToString(message.getBody()));
            req.setEntity(entity);

            return execBooleanReturnReq(req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }
}
