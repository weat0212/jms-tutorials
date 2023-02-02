package com.andywang.jms.guaranted;

import com.andywang.vo.CustomMessage;
import lombok.SneakyThrows;
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
//        normal();
        transact();
    }

    @SneakyThrows
    static void normal() {

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

    @SneakyThrows
    static void transact() {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext(JMSContext.SESSION_TRANSACTED)) {

            JMSProducer producer = jmsContext.createProducer();

            ObjectMessage objectMessage1 = jmsContext.createObjectMessage();
            objectMessage1.setObject(CustomMessage.builder().message("Message1").timestamp(new Date()).build());
            producer.send(queue, objectMessage1);

            // Try to comment this line, the message will not be sent until commit
            jmsContext.commit();

            ObjectMessage objectMessage2 = jmsContext.createObjectMessage();
            objectMessage2.setObject(CustomMessage.builder().message("Message2").timestamp(new Date()).build());
            producer.send(queue, objectMessage2);

            jmsContext.rollback();
        }
    }
}
