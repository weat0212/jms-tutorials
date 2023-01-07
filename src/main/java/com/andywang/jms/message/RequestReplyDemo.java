package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReplyDemo {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            // A: How do you do?
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage("How do you do?");
            message.setJMSReplyTo(replyQueue);
            producer.send(requestQueue, message);

            // B: (Received Message)
            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println("Received Request Message: " + messageReceived.getText());

            // B: I'm fine, thank u.
            JMSProducer replyProducer = jmsContext.createProducer();
            replyProducer.send(messageReceived.getJMSReplyTo(), "I'm fine, thank u.");

            // A: (Received Message)
            JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
            String replyMessageReceived = replyConsumer.receiveBody(String.class);
            System.out.println("Received Reply Message: " + replyMessageReceived);
        }
    }
}
