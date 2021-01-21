package com.grh.kafka_spring_demo.producer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

// kafka Producer 使用回调判断发送数据是否成功
@Component
public class KafkaProducerWithCallback {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage() {
        for (int i = 0; i < 10; i++) {
            kafkaTemplate.send("grh-topic", "key-"+i, "value-"+i)
                .addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

                    @Override
                    public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                        System.err.printf("sendMessage OK, topic=%s, partition=%d, offset=%d\n",
                                stringObjectSendResult.getRecordMetadata().topic(),
                                stringObjectSendResult.getRecordMetadata().partition(),
                                stringObjectSendResult.getRecordMetadata().offset()
                        );
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.err.println("sendMessageFail : " + throwable.getMessage());
                    }
                });
        }
    }

}
