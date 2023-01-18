package com.andywang.p2p.ap;

import com.andywang.p2p.model.Candidates;
import com.andywang.p2p.model.Education;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RecruiterApp {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext =  cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();

            Candidates candidate1 =
                    Candidates.builder().id("A123456789").age(20).education(Education.Master).name("Andy").resume("Hi, I'm now open to work").build();
            Candidates candidate2 =
                    Candidates.builder().id("B987654321").age(18).education(Education.HighSchool).name("6865").resume("I'm a bonus sucker").build();

            Candidates[] candidates = new Candidates[] { candidate1, candidate2 };

            for (Candidates candidate : candidates) {
                ObjectMessage objectMessage = jmsContext.createObjectMessage();
                objectMessage.setObject(candidate);
                producer.send(requestQueue, objectMessage);
            }

            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
            for (int i = 1; i <= candidates.length; i++) {
                MapMessage replyMessage = (MapMessage) consumer.receive(30000);
                try {
                    System.out.println("Candidate is " + (replyMessage.getBoolean("Approved") ? "Approved" : "Not Approved"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
