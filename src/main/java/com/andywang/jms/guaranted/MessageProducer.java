package com.andywang.jms.guaranted;

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

public class MessageProducer {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            objectMessage.setObject(CustomMessage.builder().message("Message").timestamp(new Date()).build());
            producer.send(queue, objectMessage);
        }
    }
}
