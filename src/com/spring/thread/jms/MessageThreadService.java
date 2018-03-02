package com.spring.thread.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
@Component

public class MessageThreadService {

    public JmsTemplate jmsTemplate;
    public Destination destination;

    public void setdestination(Destination destination) {
        this.destination = destination;
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public Destination getdestination() {
        return destination;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(final String message) {

        jmsTemplate.send(destination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

}
