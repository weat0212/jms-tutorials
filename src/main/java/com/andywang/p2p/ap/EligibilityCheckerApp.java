package com.andywang.p2p.ap;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckerApp {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext =  cf.createContext()) {

            JMSConsumer consumer = jmsContext.createConsumer(queue);
            consumer.setMessageListener(new EligibilityCheckListener());

            while (true) {
                // Make application keep running
            }
        }
    }
}
