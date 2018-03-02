package main.java.guo.examples.mq01.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class SecondSubscriber {


  public static void main(String[] args) {
    String user = ActiveMQConnection.DEFAULT_USER;
    String password = ActiveMQConnection.DEFAULT_PASSWORD;
    String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    String subject = "MQ.TOPIC";
    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
    Connection connection;
    try {
      connection = connectionFactory.createConnection();
      connection.start();
      final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
      Topic topic = session.createTopic(subject);
      // MessageConsumer负责接受消息
      MessageConsumer consumer = session.createConsumer(topic);
      consumer.setMessageListener(new MessageListener() {

        public void onMessage(Message msg) {
          TextMessage message = (TextMessage) msg;
          try {
            String hello = message.getStringProperty("hello");
            System.out.println("订阅者---SecondSubscriber---收到消息：\t" + hello);
            session.commit();
          } catch (JMSException e) {
            e.printStackTrace();
          }
        }
      });
      //session.close();
      //connection.close();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}
