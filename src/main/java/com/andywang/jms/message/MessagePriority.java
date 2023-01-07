package com.andywang.jms.message;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.stream.IntStream;

public class MessagePriority {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            java.lang.String[] messages = IntStream.rangeClosed(1, 3).mapToObj(i -> "Message" + i).toArray(String[]::new);
            // Medium (Default)
            producer.setPriority(4);
            producer.send(queue, messages[0]);
            // Lowest
            producer.setPriority(0);
            producer.send(queue, messages[1]);
            // Highest
            producer.setPriority(9);
            producer.send(queue, messages[2]);

            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);

            for (int n = 1; n <= 3; n++) {
                Message msg = jmsConsumer.receive();
                // Show Message Content and Priority Value
                System.out.printf("%s  Priority: %s \n", msg.getBody(String.class), msg.getJMSPriority());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
