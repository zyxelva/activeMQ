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

import org.springframework.jms.core.JmsTemplate;

public class ConsumerServiceImpl implements IConsumerService {
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public Destination getDestination() {
		return destination;
	}

	JmsTemplate jmsTemplate;

	Destination destination;

	public void receive() {
		MapMessage message = (MapMessage) jmsTemplate.receive();
		try {
			System.out.println("--收到消息：" + new Date(message.getLong("count")));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}


