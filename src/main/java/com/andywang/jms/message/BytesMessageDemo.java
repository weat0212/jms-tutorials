package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BytesMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            // Bytes
            BytesMessage bytesMessage = jmsContext.createBytesMessage();
            bytesMessage.writeUTF("Andy");
            bytesMessage.writeLong(212L);
            producer.send(queue, bytesMessage);

            BytesMessage messageReceived = (BytesMessage) jmsContext.createConsumer(queue).receive();
            System.out.println(messageReceived.readUTF());
            System.out.println(messageReceived.readLong());
        }
    }
}
