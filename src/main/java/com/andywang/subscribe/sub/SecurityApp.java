package com.andywang.subscribe.sub;

import com.andywang.subscribe.model.Employee;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SecurityApp {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Topic topic = (Topic) initialContext.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            JMSConsumer consumer = jmsContext.createConsumer(topic);
            Message receivedMessage = consumer.receive();
            Employee employee = receivedMessage.getBody(Employee.class);
            System.out.println(employee);
        }
    }
}
