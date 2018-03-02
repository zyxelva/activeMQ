package com.multithread.activeMQ;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 消息生产者-消息发布者（多线程发送消息）
 *
 */
public class JMSConsumerThread {

    private static final String USERNAME=ActiveMQConnection.DEFAULT_USER; // 默认的连接用户名
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // 默认的连接密码
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // 默认的连接地址

    ConnectionFactory connectionFactory=null; // 连接工厂
    private Connection connection = null;
    private Session session = null;
    private Destination destination=null; // 消息的目的地
    public void init(){
        // 实例化连接工厂
        connectionFactory=new ActiveMQConnectionFactory(JMSConsumerThread.USERNAME, JMSConsumerThread.PASSWORD, JMSConsumerThread.BROKEURL);
        try {
            connection=connectionFactory.createConnection(); // 通过连接工厂获取连接
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public void consumer(){
        MessageConsumer messageConsumer; // 消息的消费者

        try {
            session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE); // 创建Session
            destination=session.createQueue("MyQueue");
            messageConsumer=session.createConsumer(destination); // 创建消息消费者
            messageConsumer.setMessageListener(new Listener3()); // 注册消息监听
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}

