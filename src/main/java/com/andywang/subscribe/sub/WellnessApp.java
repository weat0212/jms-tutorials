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

public class WellnessApp {

    public static final String SUBSCRIPTION_NAME = WellnessApp.class.getSimpleName();

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Topic topic = (Topic) initialContext.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            JMSConsumer consumer1 = jmsContext.createSharedConsumer(topic, SUBSCRIPTION_NAME);
            JMSConsumer consumer2 = jmsContext.createSharedConsumer(topic, SUBSCRIPTION_NAME);

            /*
             * Shared Consumer
             * get message take turn
             */

            for (int i = 0; i < 5; i++) {
                Message receivedMessage1 = consumer1.receive();
                Employee employee1 = receivedMessage1.getBody(Employee.class);
                System.out.println("Consumer1: " + employee1);

                Message receivedMessage2 = consumer2.receive();
                Employee employee2 = receivedMessage2.getBody(Employee.class);
                System.out.println("Consumer2: " + employee2);
            }


            /*
             * Normal Consumer
             * consumer both got same message
             */
//            JMSConsumer consumer1 = jmsContext.createConsumer(topic);
//            JMSConsumer consumer2 = jmsContext.createConsumer(topic);
//
//            for (int i = 0; i < 10; i++) {
//                Message receivedMessage1 = consumer1.receive();
//                Employee employee1 = receivedMessage1.getBody(Employee.class);
//                System.out.println("Consumer1: " + employee1);
//
//                Message receivedMessage2 = consumer2.receive();
//                Employee employee2 = receivedMessage2.getBody(Employee.class);
//                System.out.println("Consumer2: " + employee2);
//            }
        }
    }
}
