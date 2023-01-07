package com.andywang.startup;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * P2P Protocol Example
 */
public class MyFirstQueue {

    public static void main(String[] args) throws NamingException {

        // JNDI
        InitialContext initialContext = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

        try (Connection connection = cf.createConnection()) {
            Session session = connection.createSession();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            // Producer
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("My First JMS!!!");
            producer.send(message);
            System.out.println("Message Sent: " + message.getText());

            // Consumer
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage messageReceived = (TextMessage) consumer.receive(5000);
            System.out.println("Message Received: " + messageReceived.getText());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            initialContext.close();
        }
    }
}
