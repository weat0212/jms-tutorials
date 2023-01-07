package com.andywang.startup;

import com.andywang.vo.CustomMessage;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

/**
 * Publish / Subscribe Example
 */
public class MyFirstTopic {

    public static void main(String[] args) throws NamingException {

        // JNDI
        InitialContext initialContext = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

        try (Connection connection = cf.createConnection()) {
            Session session = connection.createSession();
            Topic topic = (Topic) initialContext.lookup("topic/myTopic");

            MessageProducer producer = session.createProducer(topic);
            MessageConsumer consumer1 = session.createConsumer(topic);
            MessageConsumer consumer2 = session.createConsumer(topic);

            ObjectMessage message = session.createObjectMessage();
            message.setObject(CustomMessage.builder().timestamp(new Date()).message("My First Topic Message!!!").build());

            producer.send(message);
            connection.start();

            ObjectMessage objectMessage1 = (ObjectMessage) consumer1.receive();
            CustomMessage customMessage1 = (CustomMessage) objectMessage1.getObject();
            System.out.println("Consumer1 get message: " + customMessage1.getMessage() + customMessage1.getTimestamp());

            ObjectMessage objectMessage2 = (ObjectMessage) consumer2.receive();
            CustomMessage customMessage2 = (CustomMessage) objectMessage2.getObject();
            System.out.println("Consumer2 get message: " + customMessage2.getMessage() + customMessage2.getTimestamp());

        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            initialContext.close();
        }
    }
}
