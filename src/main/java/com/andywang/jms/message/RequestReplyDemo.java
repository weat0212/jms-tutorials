package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReplyDemo {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            producer.send(requestQueue, "How do you do?");

            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            String messageReceived = consumer.receiveBody(String.class);
            System.out.println("Received Request Message: " + messageReceived);

            JMSProducer replyProducer = jmsContext.createProducer();
            replyProducer.send(replyQueue, "I'm fine, thank u.");

            JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
            String replyMessageReceived = replyConsumer.receiveBody(String.class);
            System.out.println("Received Reply Message: " + replyMessageReceived);
        }
    }
}
