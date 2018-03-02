package com.multithread.activeMQ.performanceB;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.MapMessage;
import javax.jms.Message;

import org.dom4j.DocumentException;

import msgSend.xmlsend.XmlUtil;


public class JMSConsumerTest {
    private static  JMSConsumer consumer = new JMSConsumer();
    
    public static void main(String[] args) throws Exception {
        
        //**  JMSConsumer 可以设置成全局的静态变量，只需实例化一次即可使用,禁止循环重复实例化JMSConsumer(因为其内部存在一个线程池)
        receiveMsgFromProducer();
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sendMsgToProducer(Map map){
//            Map headMap=new HashMap();
            Map bodyMap=new HashMap();
            Map respondMap=new HashMap();
            Map msgMap=new HashMap();
            msgMap.put("Head", map);
            bodyMap.put("KeyType", "hahahahahahahahahahaha");
            msgMap.put("Body", bodyMap);
            respondMap.put("Respond", msgMap);
            String xml = null;
            String sendMsg=null;
            try {
                xml = XmlUtil.formatXml(XmlUtil.map2xml(respondMap, "Transaction"));
                Pattern p = Pattern.compile("\t|\r|\n");
                Matcher m = p.matcher(xml);
                xml = m.replaceAll("");
                sendMsg=String.format("%08d",xml.getBytes("utf-8").length)+xml;
                System.out.println("xml= "+sendMsg);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            msgMap=new HashMap();
            msgMap.put("msgMap", sendMsg);
            JMSProducer producer = new JMSProducer("tcp://localhost:61616", "system", "manager");
            System.out.println("++++++++++++++Begin to send xml++++++++++++++++++++++++");
            producer.send("xmlReceiveQueue", msgMap);
            System.out.println("++++++++++++++Done++++++++++++++++++++++++");
    }
    
    public static void receiveMsgFromProducer(){
        consumer.setBrokerUrl("tcp://localhost:61616");
        consumer.setQueue("xmlSendQueue");
        consumer.setUserName("system");
        consumer.setPassword("manager");
        consumer.setQueuePrefetch(500);
        consumer.setMessageListener(new MultiThreadMessageListener(1,new MessageHandler() {
            @SuppressWarnings("unchecked")
            public void handle(Message message) {
                try {
                    String xml=((MapMessage)message).getString("name");
                    System.out.println(Thread.currentThread().getName()+" Receive Message  is " + xml);
                    System.out.println("================================================");
                    //System.out.println("xml length= "+String.format("%08d",xml.getBytes("utf-8").length));
                    
                    Map<String, Object> map = XmlUtil.xml2map(xml, false);
                    map=(Map<String, Object>) ((Map<String, Object>) map.get("Request")).get("Head");
                    Set entrySet=map.entrySet();
                    Iterator it = entrySet.iterator();
                    while(it.hasNext()) {
                        Entry me = (Map.Entry)it.next();
                        System.out.println(me.getKey()+"="+me.getValue());
                    }
                    
                    System.out.println("================================================");
                    sendMsgToProducer(map);
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
        try {
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//      Thread.sleep(5000);
//      consumer.shutdown();
    }
    
    
}

