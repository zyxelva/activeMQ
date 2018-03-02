package com.multithread.activeMQ;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * ��Ϣ������-��Ϣ�����ߣ����̷߳��ͣ�
 * 
 */
public class JMSProducerThread {

    private static final String USERNAME=ActiveMQConnection.DEFAULT_USER; // Ĭ�ϵ������û���
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // Ĭ�ϵ���������
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // Ĭ�ϵ����ӵ�ַ
    private static final int SENDNUM=10; // ���͵���Ϣ����
    ConnectionFactory connectionFactory=null; // ���ӹ���
    private Connection connection = null;
    private Session session = null;
    private Destination destination=null; // ��Ϣ��Ŀ�ĵ�
    public void init(){
        // ʵ�������ӹ���
        connectionFactory=new ActiveMQConnectionFactory(JMSProducerThread.USERNAME, JMSProducerThread.PASSWORD, JMSProducerThread.BROKEURL);
        try {
            connection=connectionFactory.createConnection(); // ͨ�����ӹ�����ȡ����
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public void produce(){
        try {
            MessageProducer messageProducer; // ��Ϣ������
            session=connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE); // ����Session
            destination=session.createQueue("MyQueue");
            messageProducer=session.createProducer(destination); // ������Ϣ������
            for(int i=0;i<JMSProducerThread.SENDNUM;i++){
                TextMessage message=session.createTextMessage("ActiveMQ��"+Thread.currentThread().getName()+"�̷߳��͵�����"+":"+i);
                System.out.println(Thread.currentThread().getName()+"�߳�"+"������Ϣ��"+"ActiveMQ ��������Ϣ"+":"+i);
                messageProducer.send(message);
                session.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    /**
     * ������Ϣ
     * @param session
     * @param messageProducer
     * @throws Exception
     */
    public static void sendMessage(Session session,MessageProducer messageProducer)throws Exception{
        for(int i=0;i<JMSProducerThread.SENDNUM;i++){
            TextMessage message=session.createTextMessage("ActiveMQ ���͵���Ϣ"+i);
            System.out.println("������Ϣ��"+"ActiveMQ ��������Ϣ"+i);
            messageProducer.send(message);
        }
    }

}
