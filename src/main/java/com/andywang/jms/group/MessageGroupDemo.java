package com.andywang.jms.group;

import lombok.SneakyThrows;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageGroupDemo {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/groupQueue");
        ConcurrentHashMap<String, String> receivedMessages = new ConcurrentHashMap<>();

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext context1 = cf.createContext();
             JMSContext context2 = cf.createContext()) {

            JMSProducer producer = context1.createProducer();

            // Whoever received the message first, the winner get all
            JMSConsumer consumer1 = context2.createConsumer(queue);
            consumer1.setMessageListener(new MyListener("Consumer-1", receivedMessages));
            JMSConsumer consumer2 = context2.createConsumer(queue);
            consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));

//            JMSConsumer consumer2 = context2.createConsumer(queue);
//            consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));
//            JMSConsumer consumer1 = context2.createConsumer(queue);
//            consumer1.setMessageListener(new MyListener("Consumer-1", receivedMessages));

            int count = 10;
            TextMessage[] messages1 = new TextMessage[count];
            for (int i = 0; i < count; i++) {
                messages1[i] = context1.createTextMessage("Group-0 message" + i);
                messages1[i].setStringProperty("JMSXGroupID", "Group-0");
                producer.send(queue, messages1[i]);
            }

            TextMessage[] messages2 = new TextMessage[count];
            for (int i = 0; i < count; i++) {
                messages2[i] = context1.createTextMessage("Group-1 message" + i);
                messages2[i].setStringProperty("JMSXGroupID", "Group-1");
                producer.send(queue, messages2[i]);
            }

            Thread.sleep(2000);

            for (TextMessage m : messages1) {
                if (!receivedMessages.get(m.getText()).equals("Consumer-1")) {
                    System.out.println("Group Message " + m.getText() + "has gone to the wrong receiver");
                }
            }
            for (TextMessage m : messages2) {
                if (!receivedMessages.get(m.getText()).equals("Consumer-2")) {
                    System.out.println("Group Message " + m.getText() + "has gone to the wrong receiver");
                }
            }
        }
    }
}

class MyListener implements MessageListener {

    private final String name;
    private final Map<String, String> receivedMessage;

    MyListener(String name, Map<String, String> receivedMessage) {
        this.name = name;
        this.receivedMessage = receivedMessage;
    }

    @SneakyThrows
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getText());
        System.out.println("Listener Name: " + name);
        receivedMessage.put(textMessage.getText(), name);
    }
}