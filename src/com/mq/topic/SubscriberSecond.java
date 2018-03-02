package com.mq.topic;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.amazonaws.util.StringUtils;
import com.grgbanking.framework.common.util.DateUtils;

/**
 * topic:消息订阅者二
 */
public class SubscriberSecond {
	public static void main(String[] args) {
		String user = ActiveMQConnection.DEFAULT_USER;
		String password = ActiveMQConnection.DEFAULT_PASSWORD;
		String url = "tcp://localhost:61616";
		String subject = "TOOL.DEFAULT";
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, url);
		Connection connection;
		try {
			connection = factory.createConnection();
			connection.start();
			final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(subject);
			MessageConsumer consumer = session.createConsumer(topic);
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message msg) {
					MapMessage message = (MapMessage) msg;
					try {
						System.out.println("--订阅者二收到消息：" + DateUtils.format(new Date(message.getLong("count")),"yyyy-MM-dd HH:mm:ss"));
						session.commit();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
