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

public class PayrollApp {

    public static final String CLIENT_ID = PayrollApp.class.getSimpleName();
    public static final String SUBSCRIPTION_NAME = PayrollApp.class.getSimpleName();

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Topic topic = (Topic) initialContext.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {
            jmsContext.setClientID(CLIENT_ID);

            JMSConsumer consumer1 = jmsContext.createSharedDurableConsumer(topic, SUBSCRIPTION_NAME);
            JMSConsumer consumer2 = jmsContext.createSharedDurableConsumer(topic, SUBSCRIPTION_NAME);

            for (int i = 0; i < 5; i++) {

                Message receivedMessage1 = consumer1.receive();
                Employee employee1 = receivedMessage1.getBody(Employee.class);
                System.out.println("Consumer1: " + employee1);

                // Consumer1 Down
                consumer1.close();
                System.out.println("Consumer1 Down...");

                // Only if one down, another work
                Message receivedMessage2 = consumer2.receive();
                Employee employee2 = receivedMessage2.getBody(Employee.class);
                System.out.println("Consumer2: " + employee2);

                // Consumer2 Down
                consumer2.close();
                System.out.println("Consumer2 Down...");

                consumer1 = jmsContext.createSharedDurableConsumer(topic, SUBSCRIPTION_NAME);
                System.out.println("Consumer1 Up!!!");

                consumer2 = jmsContext.createSharedDurableConsumer(topic, SUBSCRIPTION_NAME);
                System.out.println("Consumer2 Up!!!");
            }

            consumer1.close();
            consumer2.close();
            jmsContext.unsubscribe(SUBSCRIPTION_NAME);
        }
    }
}
