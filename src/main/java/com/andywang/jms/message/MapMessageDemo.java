package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MapMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            // Map
            MapMessage mapMessage = jmsContext.createMapMessage();
            mapMessage.setBoolean("isMap", true);
            producer.send(queue, mapMessage);

            MapMessage mapMessageReceived = (MapMessage) jmsContext.createConsumer(queue).receive();
            System.out.println(mapMessageReceived.getBoolean("isMap"));
        }
    }
}
