package com.multithread.activeMQ.performanceB;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import msgSend.SocketService;
import msgSend.xmlsend.XmlUtil;


public class JMSProducerTest {
    private static Properties pps2 = new Properties();
    private static String cmdserverIp;
    private static String cmdserverPort;
    private static final Log logger =  LogFactory.getLog("INFO");
    private static  JMSConsumer consumer = new JMSConsumer();
    static {
        String classpath = JMSProducerTest.class.getResource("/").getPath();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(classpath + "cmdserverconfig.properties"));
            pps2.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
                logger.error("The file stream closed and an exception occurred");
            }
        }
        JMSProducerTest.cmdserverIp = pps2.getProperty("cmdserver.ip");
        JMSProducerTest.cmdserverPort = pps2.getProperty("cmdserver.port");
    }
    
    public static void main(String[] args) {
        
        getXmlString();
        //getXmlString();
        
        
//        String str="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Transaction><Respond><Head><BisUid></BisUid><MsgType></MsgType><TransCode>1604</TransCode><ExSerial>000212</ExSerial><Acctoper>663501</Acctoper><TradeType></TradeType><BranchNo>02120102</BranchNo><Channel>ATM</Channel><TermNo>663501</TermNo><TermIp>30.3.22.96</TermIp><TransDate>20171010</TransDate><TransTime>145401</TransTime><BatchNo>19</BatchNo><EncodeType>2</EncodeType></Head><Body><KeyType>hahahahahahahahahahaha</KeyType></Body></Respond></Transaction>";
//        Map<String, Object> map;
//        try {
//            map = XmlUtil.xml2map(str, false);
//            map=(Map<String, Object>) ((Map<String, Object>) map.get("Respond")).get("Head");
//            Set entrySet=map.entrySet();
//            Iterator it = entrySet.iterator();
//            while(it.hasNext()) {
//                Entry me = (Map.Entry)it.next();
//                System.out.println(me.getKey()+"="+me.getValue());
//            }
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
        
    }
    
    private static void locationTest() {
        //**  JMSProducer 可以设置成全局的静态变量，只需实例化一次即可使用,禁止循环重复实例化JMSProducer(因为其内部存在一个线程池)
        
        //支持openwire协议的默认连接为 tcp://localhost:61616，支持 stomp协议的默认连接为tcp://localhost:61613。 
        //tcp和nio的区别
        //nio://localhost:61617 以及 tcp://localhost:61616均可在 activemq.xml配置文件中进行配置
        JMSProducer producer = new JMSProducer("tcp://localhost:61616", "system", "manager");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1");
        map.put("name", "sss1113333");
        map.put("password", "password");
        producer.send("test", map);
    }
    
    private static void multiSend(){
        int size = 200000;
        String str = "[{'flag':'1','value':'8854c92e92404b188e63c4031db0eac9','label':'交换机(虚机)'},{'flag':'1','value':'3f367296c2174b7981342dc6fcb39d64','label':'防火墙'},{'flag':'1','value':'8a3e05eeedf54f8cbed37c6fb38c6385','label':'负载均衡'},{'flag':'1','value':'4f0ebc601dfc40ed854e08953f0cdce8','label':'其他设备'},{'flag':'1','value':'6','label':'路由器'},{'flag':'1','value':'4','label':'交换机'},{'flag':'1','value':'b216ca1af7ec49e6965bac19aadf66da','label':'服务器'},{'flag':'1','value':'7','label':'安全设备'},{'flag':'1','value':'cd8b768a300a4ce4811f5deff91ef700','label':'DWDM\\SDH'},{'flag':'1','value':'5','label':'防火墙(模块)'},{'flag':'1','value':'01748963956649e589a11c644d6c09b5','label':'机箱'}]";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name",str);
        long start = System.currentTimeMillis();  
        ExecutorService es = Executors.newFixedThreadPool(10);  
        final CountDownLatch cdl = new CountDownLatch(size);  
        JMSProducer producer = new JMSProducer("tcp://localhost:61616", "system", "manager");
        
        try {
            for (int a = 0; a < size; a++) {  
                es.execute(new Runnable() {  
                    @Override  
                    public void run() {  
                        producer.send("test", map);
                        cdl.countDown();  
                    }  
                });  
            }  
            cdl.await();  
            es.shutdown();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } 
        long time = System.currentTimeMillis() - start;  
        System.out.println("插入" + size + "条JSON，共消耗：" + (double)time / 1000 + " s");  
        System.out.println("平均：" + size / ((double)time/1000) + " 条/秒");  
    }
    
    public  static void xmlSend(Map map){
        //String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Transaction version=\"1.0\"><Request><Head><BisUid></BisUid><MsgType></MsgType><TransCode>1604</TransCode><ExSerial>000212</ExSerial><Acctoper>663501</Acctoper><TradeType></TradeType><BranchNo>02120102</BranchNo><Channel>ATM</Channel><TermNo>663501</TermNo><TermIp>30.3.22.96</TermIp><TransDate>20171010</TransDate><TransTime>145401</TransTime><BatchNo>19</BatchNo><EncodeType>2</EncodeType></Head><Body><KeyType>PIN</KeyType></Body></Request></Transaction>";
        Map  map2 = new HashMap();
        //map=getXmlString();
        map2.put("name",map.get("XML"));
        
        JMSProducer producer = new JMSProducer("tcp://localhost:61616", "system", "manager");
        System.out.println("++++++++++++++Begin to send xml++++++++++++++++++++++++");
        producer.send("xmlSendQueue", map2);
        System.out.println("++++++++++++++Done++++++++++++++++++++++++");
    }
    
    public static  void  getXmlString(){
        InputStream is = null;
        Map<String, Object> map=new HashMap<String, Object>();
        Socket s2=null;
        ServerSocket s = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try{
           // s2 = new Socket(cmdserverIp, Integer.parseInt(cmdserverPort));
            s=new ServerSocket(5656);
            s.setSoTimeout(60 * 1000);
            
            while (true) {
                s2=s.accept();
                int size = 8;
                byte b[] = new byte[size];
                is = s2.getInputStream();
    
                if (is == null) {
                    throw new Exception();
                }
                int length = 0;
                length = is.read(b);
                if(length<0){
                    throw new Exception();
                }
                bo.write(b, 0, length);
                String resp = new String(bo.toByteArray());
                size = Integer.parseInt(resp);
                int offset = 0;
                String respbf = new String();
                b = new byte[size];
                while(offset < size){
                    bo.reset();
                    length = is.read(b);
                    offset += length;
                    respbf += new String(b).substring(0, length);
                }
                if (StringUtils.isNotEmpty(respbf)) {
                    map.put("XML", respbf); //XmlUtil.xml2map(respbf.toString(), false);
                    //return map;
                    xmlSend(map);
                    System.out.println("++++++++++++++Begin to receive msg from consumer++++++++++++++++++++++++");
                    receiveMsgFromConsumer();
                    System.out.println("over.");
                   // return;
                }
            }
        }catch (ConnectException connExc) {
            System.out.println("ConnectException");
        } catch (SocketTimeoutException timeOutExc) {
            System.out.println("SocketTimeout");
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null){
                    is.close();
                    is = null;
                }
                if(bo != null){
                    bo.close();
                    bo = null;
                }
//                if(os != null){
//                    os.close();
//                    os = null;
//                }
                if(s2 != null){
                    s2.close();
                    s2 = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //printMap(map);
        //return map;
    }
    
    /*public static void getXmlString2(){
        ServerSocket ss = null;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            ss = new ServerSocket(5555);
                while (true) {
                    socket = ss.accept();
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    String line = in.readLine();
                    System.out.println("you input is :" + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }*/
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void printMap(Map map){
        map=(Map<String, Object>) ((Map<String, Object>) map.get("Request")).get("Head");
        Set entrySet=map.entrySet();
        Iterator it = entrySet.iterator();
        while(it.hasNext()) {
            Entry me = (Map.Entry)it.next();
            System.out.println(me.getKey()+"="+me.getValue());
        }
    }
    
    public static void receiveMsgFromConsumer(){
        consumer.setBrokerUrl("tcp://localhost:61616");
        consumer.setQueue("xmlReceiveQueue");
        consumer.setUserName("system");
        consumer.setPassword("manager");
        consumer.setQueuePrefetch(500);
        consumer.setMessageListener(new MultiThreadMessageListener(1,new MessageHandler() {
            @SuppressWarnings("unchecked")
            public void handle(Message message) {
                try {
                    String xml=((MapMessage)message).getString("msgMap");
                    System.out.println(Thread.currentThread().getName()+" Receive Message  is " + xml);
                    System.out.println("================================================");
                    //System.out.println("xml length= "+String.format("%08d",xml.getBytes("utf-8").length));
                    
                    Map<String, Object> map = XmlUtil.xml2map(xml.substring(8), false);
                    map=(Map<String, Object>) ((Map<String, Object>) map.get("Respond")).get("Head");
                    Set entrySet=map.entrySet();
                    Iterator it = entrySet.iterator();
                    while(it.hasNext()) {
                        Entry me = (Map.Entry)it.next();
                        System.out.println(me.getKey()+"="+me.getValue());
                    }
                    new SocketService().sendXml(cmdserverIp, cmdserverPort, xml, null);
                    System.out.println("================================================");
                    //sendMsgToProducer(map);
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
    }
}
