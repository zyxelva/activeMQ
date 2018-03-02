package com.mq.queue;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * queue:消息消费者
 */
public class Producer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String user = ActiveMQConnection.DEFAULT_USER;
		String password = ActiveMQConnection.DEFAULT_PASSWORD;
		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		String subject = "TOOL.DEFAULT";
		ConnectionFactory contectionFactory = new ActiveMQConnectionFactory( user, password, url);
		try {
			Connection connection = contectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(subject);
			MessageProducer producer = session.createProducer(destination);
			for (int i = 0; i <= 20; i++) {
				MapMessage message = session.createMapMessage();
				Date date = new Date(); 
				message.setLong("count", i);
				Thread.sleep(1000);
				//message.setJMSExpiration(1000L);
				producer.send(message);
				System.out.println("--发送消息：Hello Boy!" + date);
				
//				TextMessage message2 = session.createTextMessage();
//				message2.setText("HHHHHHHHHHHH_"+i);
//				Thread.sleep(1000);
//                //message.setJMSExpiration(1000L);
//                producer.send(message2);
//                System.out.println("--发送消息：Hello Boy! Count_" + i);
			}
			session.commit();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

