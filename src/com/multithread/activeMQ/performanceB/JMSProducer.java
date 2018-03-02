package com.multithread.activeMQ.performanceB;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import msgSend.SocketService;

/**
 * JMS��Ϣ������
 * @author linwei
 *
 */
public class JMSProducer implements ExceptionListener{
    
    //�������ӵ����������
    public final static int DEFAULT_MAX_CONNECTIONS=5;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    //����ÿ��������ʹ�õ�����Ự��
    private int maximumActiveSessionPerConnection = DEFAULT_MAXIMUM_ACTIVE_SESSION_PER_CONNECTION;
    public final static int DEFAULT_MAXIMUM_ACTIVE_SESSION_PER_CONNECTION=300;
    //�̳߳�����
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
    public final static int DEFAULT_THREAD_POOL_SIZE=50;
    //ǿ��ʹ��ͬ���������ݵĸ�ʽ
    private boolean useAsyncSendForJMS = DEFAULT_USE_ASYNC_SEND_FOR_JMS;
    public final static boolean DEFAULT_USE_ASYNC_SEND_FOR_JMS=true;
    //�Ƿ�־û���Ϣ
    private boolean isPersistent = DEFAULT_IS_PERSISTENT;
    public final static boolean DEFAULT_IS_PERSISTENT=true; 
    
    //���ӵ�ַ
    private String brokerUrl;

    private String userName;

    private String password;

    private ExecutorService threadPool;

    private PooledConnectionFactory connectionFactory;
    
    public JMSProducer(String brokerUrl, String userName, String password) {
        this(brokerUrl, userName, password, DEFAULT_MAX_CONNECTIONS, DEFAULT_MAXIMUM_ACTIVE_SESSION_PER_CONNECTION, DEFAULT_THREAD_POOL_SIZE, DEFAULT_USE_ASYNC_SEND_FOR_JMS, DEFAULT_IS_PERSISTENT);
    }
    
    public JMSProducer(String brokerUrl, String userName, String password, int maxConnections, int maximumActiveSessionPerConnection, int threadPoolSize,boolean useAsyncSendForJMS, boolean isPersistent) {
        this.useAsyncSendForJMS = useAsyncSendForJMS;
        this.isPersistent = isPersistent;
        this.brokerUrl = brokerUrl;
        this.userName = userName;
        this.password = password;
        this.maxConnections = maxConnections;
        this.maximumActiveSessionPerConnection = maximumActiveSessionPerConnection;
        this.threadPoolSize = threadPoolSize;
        init();
    }
      
    private void init() {
        //����JAVA�̳߳�
        this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        //ActiveMQ�����ӹ���
        ActiveMQConnectionFactory actualConnectionFactory = new ActiveMQConnectionFactory(this.userName, this.password, this.brokerUrl);
        actualConnectionFactory.setUseAsyncSend(this.useAsyncSendForJMS);
        //Active�е����ӳع���
        this.connectionFactory = new PooledConnectionFactory(actualConnectionFactory);
        this.connectionFactory.setCreateConnectionOnStartup(true);
        this.connectionFactory.setMaxConnections(this.maxConnections);
        this.connectionFactory.setMaximumActiveSessionPerConnection(this.maximumActiveSessionPerConnection);
    }
    
    
    /**
     * ִ�з�����Ϣ�ľ��巽��
     * @param queue
     * @param map
     */
    public void send(final String queue, final Map<String, Object> map) {
        //ֱ��ʹ���̳߳���ִ�о���ĵ���
        this.threadPool.execute(new Runnable(){
            @Override
            public void run() {
                try {
                    sendMsg(queue,map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * ������ִ����Ϣ����
     * @param queue
     * @param map
     * @throws Exception
     */
    private void sendMsg(String queue, Map<String, Object> map) throws Exception {
        
        Connection connection = null;
        Session session = null;
        try {
            //�����ӳع����л�ȡһ������
            connection = this.connectionFactory.createConnection();
            /*createSession(boolean transacted,int acknowledgeMode)
              transacted - indicates whether the session is transacted acknowledgeMode - indicates whether the consumer or the client 
              will acknowledge any messages it receives; ignored if the session is transacted. 
              Legal values are Session.AUTO_ACKNOWLEDGE, Session.CLIENT_ACKNOWLEDGE, and Session.DUPS_OK_ACKNOWLEDGE.
            */
            //false ������ʾ Ϊ����������Ϣ������Ĳ�����ʾ��Ϣ��ȷ������
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            //Destination is superinterface of Queue
            //PTP��Ϣ��ʽ     
            Destination destination = session.createQueue(queue);
            //Creates a MessageProducer to send messages to the specified destination
            MessageProducer producer = session.createProducer(destination);
            //set delevery mode
            producer.setDeliveryMode(this.isPersistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            //map convert to javax message
            Message message = getMessage(session, map);
            producer.send(message);
        } finally {
            closeSession(session);
            closeConnection(connection);
        }
    }
    
    private Message getMessage(Session session, Map<String, Object> map) throws JMSException {
        MapMessage message = session.createMapMessage();
        if (map != null && !map.isEmpty()) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                message.setObject(key, map.get(key));
            }
        }
        return message;
    }
    
    private void closeSession(Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onException(JMSException e) {
        e.printStackTrace();
    }

}
