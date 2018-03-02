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
 * ��Ϣ������-��Ϣ�����ߣ����̷߳�����Ϣ��
 *
 */
public class JMSConsumerThread {

    private static final String USERNAME=ActiveMQConnection.DEFAULT_USER; // Ĭ�ϵ������û���
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // Ĭ�ϵ���������
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // Ĭ�ϵ����ӵ�ַ

    ConnectionFactory connectionFactory=null; // ���ӹ���
    private Connection connection = null;
    private Session session = null;
    private Destination destination=null; // ��Ϣ��Ŀ�ĵ�
    public void init(){
        // ʵ�������ӹ���
        connectionFactory=new ActiveMQConnectionFactory(JMSConsumerThread.USERNAME, JMSConsumerThread.PASSWORD, JMSConsumerThread.BROKEURL);
        try {
            connection=connectionFactory.createConnection(); // ͨ�����ӹ�����ȡ����
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public void consumer(){
        MessageConsumer messageConsumer; // ��Ϣ��������

        try {
            session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE); // ����Session
            destination=session.createQueue("MyQueue");
            messageConsumer=session.createConsumer(destination); // ������Ϣ������
            messageConsumer.setMessageListener(new Listener3()); // ע����Ϣ����
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}

