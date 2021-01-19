package com.javacamp.demo.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class QueueProducer {

    private static final String url = "tcp://192.168.3.144:61616";
    private static final String queueName = "test-queue";

    public static void main(String[] args) throws JMSException {
        ConnectionFactory cf = new ActiveMQConnectionFactory(url);
        Connection conn = cf.createConnection();
        conn.start();

        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        MessageProducer messageProducer = session.createProducer(destination);

        for (int i = 0; i < 100; i++) {
            TextMessage msg = session.createTextMessage("test" + i);
            messageProducer.send(msg);
        }

        session.close();
        conn.close();
    }
}
