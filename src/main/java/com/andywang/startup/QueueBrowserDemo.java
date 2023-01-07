package com.andywang.startup;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Enumeration;

/**
 * Queue Browser Example
 */
public class QueueBrowserDemo {

    public static void main(String[] args) throws NamingException {

        // JNDI
        InitialContext initialContext = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

        try (Connection connection = cf.createConnection()) {
            Session session = connection.createSession();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            MessageProducer producer = session.createProducer(queue);
            TextMessage message1 = session.createTextMessage("Message 1");
            TextMessage message2 = session.createTextMessage("Message 2");
            TextMessage message3 = session.createTextMessage("Message 3");
            producer.send(message1);
            producer.send(message2);
            producer.send(message3);

            // Browser can peak the message without deleting them
            QueueBrowser browser = session.createBrowser(queue);
            Enumeration messageEnum = browser.getEnumeration();
            while (messageEnum.hasMoreElements()) {
                TextMessage textMsg = (TextMessage) messageEnum.nextElement();
                System.out.println("Browsing: " + textMsg.getText());
            }

            // Message deleted only when consumer received message
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage messageReceived1 = (TextMessage) consumer.receive(5000);
            System.out.println("Message Received: " + messageReceived1.getText());
            TextMessage messageReceived2 = (TextMessage) consumer.receive(5000);
            System.out.println("Message Received: " + messageReceived2.getText());
            TextMessage messageReceived3 = (TextMessage) consumer.receive(5000);
            System.out.println("Message Received: " + messageReceived3.getText());
            // Message consumed
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            initialContext.close();
        }
    }
}
