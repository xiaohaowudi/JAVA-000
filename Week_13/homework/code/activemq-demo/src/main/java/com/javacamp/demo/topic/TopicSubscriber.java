package com.javacamp.demo.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicSubscriber {
    private static final String url = "tcp://192.168.3.144:61616";
    private static final String destName = "test-topic";

    public static void main(String[] args) throws JMSException {
        ConnectionFactory cf = new ActiveMQConnectionFactory(url);
        Connection conn = cf.createConnection();
        conn.start();

        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(destName);
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener((Message msg) -> {
            if (msg instanceof TextMessage) {
                try {
                    System.out.println(((TextMessage) msg).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        // 回话和消息都不能关，否则受不到消息
        //session.close();
        //conn.close();
    }
}
