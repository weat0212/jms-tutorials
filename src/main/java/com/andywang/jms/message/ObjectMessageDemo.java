package com.andywang.jms.message;

import com.andywang.vo.CustomMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

public class ObjectMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            // Object
            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            objectMessage.setObject(
                    CustomMessage.builder().timestamp(new Date()).message("I'm an object").build()
            );
            producer.send(queue, objectMessage);

            CustomMessage objectMessageReceived = jmsContext.createConsumer(queue).receiveBody(CustomMessage.class);
            System.out.println(objectMessageReceived);
        }
    }
}
