package com.multithread.activeMQ.performance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueProducerSender {  
  
    static int size = 300000;  
    static Session session;  
    static MessageProducer producer;  
    static Queue queue;  
    static Connection connection;  
    static String str = "[{'flag':'1','value':'8854c92e92404b188e63c4031db0eac9','label':'������(���)'},{'flag':'1','value':'3f367296c2174b7981342dc6fcb39d64','label':'����ǽ'},{'flag':'1','value':'8a3e05eeedf54f8cbed37c6fb38c6385','label':'���ؾ���'},{'flag':'1','value':'4f0ebc601dfc40ed854e08953f0cdce8','label':'�����豸'},{'flag':'1','value':'6','label':'·����'},{'flag':'1','value':'4','label':'������'},{'flag':'1','value':'b216ca1af7ec49e6965bac19aadf66da','label':'������'},{'flag':'1','value':'7','label':'��ȫ�豸'},{'flag':'1','value':'cd8b768a300a4ce4811f5deff91ef700','label':'DWDM\\SDH'},{'flag':'1','value':'5','label':'����ǽ(ģ��)'},{'flag':'1','value':'01748963956649e589a11c644d6c09b5','label':'����'}]";  
  
     public static void init_connection() throws Exception {  
         ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");  
         connection = factory.createConnection();
         connection.start();  
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
         queue = session.createQueue("PerformanceQueue");  
         producer = session.createProducer(queue);
         producer.setDeliveryMode(DeliveryMode.PERSISTENT);  
     }  
   
     public static void sendMessage(String msg) {  
         TextMessage message;  
         try {  
             message = session.createTextMessage();  
             message.setText(str);  
             producer.send(message);  
         } catch (JMSException e) {  
             e.printStackTrace();  
         }  
     }  
   
     public static void close() throws Exception {  
         connection.close();  
     }  
   
     public static void main(String[] arg) throws Exception {  
         long start = System.currentTimeMillis();  
         ExecutorService es = Executors.newFixedThreadPool(10);  
         final CountDownLatch cdl = new CountDownLatch(size);  
         init_connection();  
         for (int a = 0; a < size; a++) {  
             es.execute(new Runnable() {  
                 @Override  
                 public void run() {  
                     sendMessage(str);  
                     cdl.countDown(); 
                     //System.out.println("�����߳�..."+Thread.currentThread().getName());
                 }  
             });  
         }  
         cdl.await();  
         es.shutdown();  
         long time = System.currentTimeMillis() - start;  
         System.out.println("����" + size + "��JSON�������ģ�" + (double)time / 1000 + " s");  
         System.out.println("ƽ����" + size / ((double)time/1000) + " ��/��");  
         close();  
     }  
 }  
