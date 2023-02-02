package com.andywang.jms.guaranted;

import com.andywang.vo.CustomMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageConsumer {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
            // default: JMSContext.AUTO_ACKNOWLEDGE

            JMSConsumer consumer = jmsContext.createConsumer(queue);

            Message receivedMessage = consumer.receive();
            CustomMessage customMessage = receivedMessage.getBody(CustomMessage.class);
            System.out.println(customMessage.toString());

            // Try to commit this line, and run this twice.
            // Result: consumer will keep received same message until ack is sent back to JMS Provider
            receivedMessage.acknowledge();
        }
    }
}
