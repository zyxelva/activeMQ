package com.multithread.activeMQ.performance;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MyConsumer {
      static int size = 200000;  
      private static ExecutorService threadPool =Executors.newFixedThreadPool(10);
      public static void main(String[] args) {
        long start = System.currentTimeMillis(); 
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        String url = "tcp://localhost:61616?jms.prefetchPolicy.all=2";
        String subject = "PerformanceQueue";
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection;
        try {
            connection = factory.createConnection();
            connection.start();
            final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(subject);
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    
                   
                        threadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    try {
                                        Thread.sleep(new Random().nextInt(2)*500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("接收线程..."+Thread.currentThread().getName()+"收到的消息是："+((TextMessage)message).getText());
                                } catch (JMSException e) {
                                    e.printStackTrace();
                                }               
                            }
                        }); 
                   
                    //threadPool.shutdown();
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
        
        
        long time = System.currentTimeMillis() - start;
        System.out.println("插入" + size + "条JSON，共消耗：" + (double)time / 1000 + " s");  
        System.out.println("平均：" + size / ((double)time/1000) + " 条/秒"); 
      }
}

