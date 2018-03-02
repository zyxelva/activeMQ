package com.mq.queue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.Message;

/**
 * queue:消息消费者
 */
public class MyProducer {
    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private ArrayList<?> jobs;
    
    public MyProducer() throws JMSException {  
           String brokerURL = ActiveMQConnection.DEFAULT_BROKER_URL;
           factory = new ActiveMQConnectionFactory(brokerURL);  
           connection = factory.createConnection();  
           connection.start();  
           session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
           producer = session.createProducer(null);  
           jobs=new ArrayList<String>(Arrays.asList("adam","bern","book","polly","ken"));
       }  

    public void sendMessage() throws JMSException {
        for(int i = 0; i < jobs.size(); i++)
        {
            String job = (String) jobs.get(i);
            Destination destination = session.createQueue("JOBS." + job);
            Message message = (Message) session.createObjectMessage(i);
            System.out.println("Sending: id: " + ((ObjectMessage)message).getObject() + " on queue: " + destination);
            producer.send(destination, message);
        }
    }

	/**
	 * @param args
	 * @throws JMSException 
	 */
	public static void main(String[] args) throws JMSException {
//		String user = ActiveMQConnection.DEFAULT_USER;
//		String password = ActiveMQConnection.DEFAULT_PASSWORD;
//		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
//		String subject = "TOOL.DEFAULT";
//		ConnectionFactory contectionFactory = new ActiveMQConnectionFactory( user, password, url);
//		try {
//			Connection connection = contectionFactory.createConnection();
//			connection.start();
//			Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
//			Destination destination = session.createQueue(subject);
//			MessageProducer producer = session.createProducer(destination);
//			for (int i = 0; i <= 20; i++) {
//				MapMessage message = session.createMapMessage();
//				Date date = new Date(); 
//				message.setLong("count", i);
//				Thread.sleep(1000);
//				producer.send(message);
//				System.out.println("--发送消息：Hello Boy!" + date);
//			}
//			session.commit();
//			session.close();
//			connection.close();
//		} catch (JMSException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	    
	        MyProducer producer = new MyProducer();
	        for(int i = 0; i < 10; i++) {
	            producer.sendMessage();
	            System.out.println("Published " + i + " job messages");
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        ((Connection) producer).close();
	    }
}

