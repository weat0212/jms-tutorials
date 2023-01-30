package com.andywang.subscribe.pub;

import com.andywang.subscribe.model.Employee;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.stream.IntStream;

public class HrManageApp {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Topic topic = (Topic) initialContext.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            IntStream.rangeClosed(1, 10).forEach(x -> {
                Employee employee =
                        Employee.builder().id(x).firstName("Andy").lastName("Wang")
                                .designation("Software Engineer").email("weat0212@gmail.com").phone("0912345678").build();
                jmsContext.createProducer().send(topic, employee);
            });

            System.out.println("Message Sent");
        }
    }
}
