package com.andywang.jms.v2;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSContextDemo {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            // Producer
            jmsContext.createProducer().send(queue, "Text Message");

            // Consumer
            String messageReceived = jmsContext.createConsumer(queue).receiveBody(String.class);
            System.out.println("Received Message: " + messageReceived);
        }
    }
}
