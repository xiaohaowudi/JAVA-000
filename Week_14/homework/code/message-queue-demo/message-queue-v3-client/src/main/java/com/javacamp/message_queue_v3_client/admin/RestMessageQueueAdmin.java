package com.javacamp.message_queue_v3_client.admin;


import com.javacamp.message_queue_core_api.admin.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class RestMessageQueueAdmin implements Admin {

    @Value("${message_queue.host}")
    private String messageQueueHost;

    @Value("${message_queue.port}")
    private Integer messageQueuePort;

    private final HttpClient client = new DefaultHttpClient();

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
    public boolean addBroker(String brokerId) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/broker?";
        uri += "broker=" + brokerId;

        return execBooleanReturnReq(new HttpPut(uri));
    }

    @Override
    public boolean deleteBroker(String brokerId) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/broker?";
        uri += "broker=" + brokerId;

        return execBooleanReturnReq(new HttpDelete(uri));
    }

    @Override
    public boolean addTopic(String brokerId, String topic) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/topic?";
        uri += "broker=" + brokerId;
        uri += "&topic=" + topic;

        return execBooleanReturnReq(new HttpPut(uri));
    }

    @Override
    public boolean deleteTopic(String brokerId, String topic) {
        String uri = "http://" + messageQueueHost + ":" + messageQueuePort + "/topic?";
        uri += "broker=" + brokerId;
        uri += "&topic=" + topic;

        return execBooleanReturnReq(new HttpDelete(uri));
    }
}
