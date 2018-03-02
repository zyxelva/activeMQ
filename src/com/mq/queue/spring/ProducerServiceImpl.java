/**
 * 
 */
package com.mq.queue.spring;

/**
 * @author KEN
 *
 */

import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ProducerServiceImpl implements IProducerService {

	JmsTemplate jmsTemplate;
	 
	Destination destination;
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	public Destination getDestination() {
		return destination;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
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
}

