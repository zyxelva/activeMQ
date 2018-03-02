package com.multithread.activeMQ.performanceB;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;


/**
 * JMS��Ϣ������
 * @author linwei
 *
 */
public class JMSConsumer implements ExceptionListener {

    //����Ԥȡ����
    private int queuePrefetch=DEFAULT_QUEUE_PREFETCH;
    public final static int DEFAULT_QUEUE_PREFETCH=10;
    
    private String brokerUrl;
    
    private String userName;

    private String password;

    private MessageListener messageListener;
    
    private Connection connection;
    
    private Session session;
    //������
    private String queue;
    
    
    /**
     * ִ����Ϣ��ȡ�Ĳ���
     * @throws Exception
     */
    public void start() throws Exception {
        //ActiveMQ�����ӹ���
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.userName, this.password, this.brokerUrl);
        connection = connectionFactory.createConnection();
        //activeMQԤȡ����
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(queuePrefetch);
        ((ActiveMQConnection) connection).setPrefetchPolicy(prefetchPolicy);
        connection.setExceptionListener(this);
        connection.start();
        //�Ự���÷����񼶱���Ϣ�������ʹ���Զ�֪ͨ����
        session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(this.queue);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(this.messageListener);
    }
    
    
    /**
     * �ر�����
     */
    public void shutdown(){
        try {
            if (session != null) {
                session.close();
                session=null;
            }
            if (connection != null) {
                connection.close();
                connection=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onException(JMSException e) {
        e.printStackTrace();
    }


    public String getBrokerUrl() {
        return brokerUrl;
    }


    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getQueue() {
        return queue;
    }


    public void setQueue(String queue) {
        this.queue = queue;
    }


    public MessageListener getMessageListener() {
        return messageListener;
    }


    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }


    public int getQueuePrefetch() {
        return queuePrefetch;
    }


    public void setQueuePrefetch(int queuePrefetch) {
        this.queuePrefetch = queuePrefetch;
    }
    
    
}
