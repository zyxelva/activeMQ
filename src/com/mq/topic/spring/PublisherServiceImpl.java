package com.mq.topic.spring;

import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.mq.topic.spring.IPublisherService;

public class PublisherServiceImpl implements IPublisherService {

	JmsTemplate jmsTemplate;
	 
	Destination destination;
	
	public void send() {
		MessageCreator messageCreator = new MessageCreator(){
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				Date date = new Date();
				message.setLong("count", date.getTime());
				System.out.println("--·¢ËÍÏûÏ¢£º"+date);
				return message;
			}
		};
		jmsTemplate.send(this.destination,messageCreator);
	}
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}
