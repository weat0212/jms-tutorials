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

    public static final String SUBSCRIPTION_NAME = SecurityApp.class.getSimpleName();
    public static final String CLIENT_ID = SecurityApp.class.getSimpleName();

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Topic topic = (Topic) initialContext.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            // Durable subscription
            jmsContext.setClientID(CLIENT_ID);

            JMSConsumer consumer = jmsContext.createDurableConsumer(topic, SUBSCRIPTION_NAME);

            // Mocking server down
            consumer.close();
            System.out.println("Server Down...");
            Thread.sleep(10000);

            // Restart
            consumer = jmsContext.createDurableConsumer(topic, SUBSCRIPTION_NAME);
            System.out.println("Server Up!!!");

            Message receivedMessage = consumer.receive();
            Employee employee = receivedMessage.getBody(Employee.class);
            System.out.println(employee);

            consumer.close();
            jmsContext.unsubscribe(SUBSCRIPTION_NAME);
        }
    }
}
