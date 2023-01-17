package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageProperties {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            TextMessage textMessage = jmsContext.createTextMessage("Text Message");
            textMessage.setBooleanProperty("loggedIn", true);
            textMessage.setStringProperty("jwtToken", "1az2wsx3edcrf");
            producer.send(queue, textMessage);

            Message messageReceived = jmsContext.createConsumer(queue).receive();
            System.out.println(messageReceived.getBody(String.class));
            System.out.println(messageReceived.getBooleanProperty("loggedIn"));
            System.out.println(messageReceived.getStringProperty("jwtToken"));
        }
    }
}
