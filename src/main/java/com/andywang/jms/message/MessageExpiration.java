package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageExpiration {

    public static void main(String[] args) throws NamingException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");
        Queue expiryQueue = (Queue) initialContext.lookup("queue/expiryQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            // Set timeout
            producer.setTimeToLive(2000);
            producer.send(queue, "I'm dead after 2s");

            // Must be expired
            Thread.sleep(3000);

            // Wait only 5s
            Message messageReceived = jmsContext.createConsumer(queue).receive(5000);
            System.out.println("Got message? " + messageReceived);

            // Retrieve expiry message
            System.out.println(jmsContext.createConsumer(expiryQueue).receiveBody(String.class));
        }
    }
}
