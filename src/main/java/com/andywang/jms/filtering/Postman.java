package com.andywang.jms.filtering;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Postman {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/mailQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            // 1. Boolean
            objectMessage.setBooleanProperty("international", false);
            // 2. Double
            objectMessage.setDoubleProperty("postage", 200);
            // 3. String
            objectMessage.setStringProperty("sender", "Andy");
            objectMessage.setStringProperty("receiverAddress", "Taiwan");

            Mail mail = Mail.builder()
                            .mailId(1).sender("Andy").senderAddress("Taiwan")
                            .receiver("Jack").receiverAddress("Taiwan")
                            .international(false).postage(100).build();

            objectMessage.setObject(mail);
            producer.send(queue, objectMessage);

            // 1. Boolean
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "international=false");
            // 2. Double
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "postage BETWEEN 50 AND 150");
            // 3. String
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "sender LIKE 'A%'");
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "sender LIKE 'And_'");
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "receiverAddress IN ('Taiwan','USA')");

            // filtering specific header
            JMSConsumer consumer = jmsContext.createConsumer(queue, "JMSPriority BETWEEN 3 AND 5");

            Mail receivedMail = consumer.receiveBody(Mail.class, 5000);
            System.out.println(receivedMail.toString());
        }
    }
}
