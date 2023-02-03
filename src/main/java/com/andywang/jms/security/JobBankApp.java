package com.andywang.jms.security;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class JobBankApp {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/authRequestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/authReplyQueue");

        Random rand = new Random();

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext =  cf.createContext("jobbankuser", "password")) {

            JMSProducer producer = jmsContext.createProducer();

            Candidates candidate1 =
                    Candidates.builder().id("A123456789").age(20).education(Education.Master).name("Andy").resume("Hi, I'm now open to work").build();
            Candidates candidate2 =
                    Candidates.builder().id("B987654321").age(18).education(Education.HighSchool).name("6865").resume("I'm a bonus sucker").build();

            List<Candidates> candidates = new ArrayList<>();
            candidates.add(candidate1);
            candidates.add(candidate2);
            IntStream.rangeClosed(1, 10).forEach(i -> {
                candidates.add(
                        Candidates.builder()
                                .id("E" + Math.round(Math.random() * 1000000000))
                                .name("ddos")
                                .age(25)
                                .education(Education.findByCode(rand.nextInt(4) + 1)).build());
            });

            for (Candidates candidate : candidates) {
                ObjectMessage objectMessage = jmsContext.createObjectMessage();
                objectMessage.setObject(candidate);
                producer.send(requestQueue, objectMessage);
            }

            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
            for (int i = 1; i <= candidates.size(); i++) {
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
