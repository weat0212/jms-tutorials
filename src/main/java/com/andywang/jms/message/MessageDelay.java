package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageDelay {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            producer.setDeliveryDelay(3000);
            producer.send(queue, "Send out message after 3s");

            String messageReceived = jmsContext.createConsumer(queue).receiveBody(String.class);
            System.out.println(messageReceived);
        }
    }
}
