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
import java.util.HashMap;
import java.util.Map;

public class RequestReplyDemo {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            // A: How do you do?
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage("How do you do?");
            Queue replyQueue = jmsContext.createTemporaryQueue();
            message.setJMSReplyTo(replyQueue);
            producer.send(requestQueue, message);
            System.out.println(message.getJMSMessageID());

//            Map<String, TextMessage> requestMsg = new HashMap<>();
//            requestMsg.put(message.getJMSMessageID(), message);

            // B: (Received Message)
            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println("Received Request Message: " + messageReceived.getText());

            // B: I'm fine, thank u.
            JMSProducer replyProducer = jmsContext.createProducer();
            TextMessage replyMessage = jmsContext.createTextMessage("I'm fine, thank u.");
            // recognized by ID
            replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
            replyProducer.send(messageReceived.getJMSReplyTo(), replyMessage);

            // A: (Received Message)
            JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
            TextMessage replyReceived = (TextMessage) replyConsumer.receive();
            System.out.println(replyReceived.getJMSCorrelationID());
            System.out.println("Received Reply Message: " + replyReceived.getText());
//            System.out.println(requestMsg.get(replyReceived.getJMSCorrelationID()).getText());
        }
    }
}
