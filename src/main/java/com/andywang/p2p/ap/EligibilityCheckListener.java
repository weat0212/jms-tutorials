package com.andywang.p2p.ap;

import com.andywang.p2p.model.Candidates;
import com.andywang.p2p.model.Education;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext =  cf.createContext()) {

            InitialContext initialContext = new InitialContext();
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
            MapMessage mapMessage = jmsContext.createMapMessage();

            Candidates candidate = (Candidates) objectMessage.getObject();
            System.out.println("Resume Received: " + candidate);

            if (Education.Master.equals(candidate.getEducation())) {
                if (candidate.getAge() > 18 && candidate.getAge() < 30) {
                    mapMessage.setBoolean("Approved", true);
                } else {
                    mapMessage.setBoolean("Approved", false);
                }
            }
            jmsContext.createProducer().send(replyQueue, mapMessage);

        } catch (JMSException | NamingException ex) {
            ex.printStackTrace();
        }
    }
}
