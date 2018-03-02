package com.grgbanking.gaps.core.adapter.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import msgSend.SocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class SimpleInletHandler extends ChannelInboundHandlerAdapter implements MessageListener {

	private static String brokerURL ;
	private static String requestQueueName ;
	private static String responseQueueName ;

	private Connection connection;
	private Session session;
	private Destination requestQueue;
    private Destination responseQueue;
    private MessageProducer requestProducer;
    private MessageConsumer responseConsumer;
    private static final HashMap<String, ChannelHandlerContext> handlerContexts = new HashMap<String, ChannelHandlerContext>();
//zyxin
    private static GenericPackager customPackager;
    public static final String PREFIX ;
    public static final String pathName;
    
    static{
        String classpath = SimpleInletHandler.class.getResource("/").getPath();
        PREFIX=classpath+"cfg/packager/";
        pathName=PREFIX+"iso87ascii-grgbanking.xml";
        brokerURL = "tcp://localhost:61616";
        requestQueueName = "TCPInletHandler.requestQueue";
        responseQueueName = "TCPInletHandler.responseQueue";
    }
    
    public SimpleInletHandler() {
    	try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
	    	connection = connectionFactory.createConnection();
	    	connection.start();
	    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			requestQueue = session.createQueue(requestQueueName);
			responseQueue = session.createQueue(responseQueueName);

			requestProducer = session.createProducer(requestQueue);
			requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            responseConsumer = session.createConsumer(responseQueue);
            responseConsumer.setMessageListener(this);
    	} catch (JMSException e) {
			e.printStackTrace();
    	}
    }
    
	@Override
	public synchronized void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		ByteBuf requestBuffer = (ByteBuf)message;
		int requestLength = requestBuffer.readableBytes();

		byte[] requestBytes = new byte[requestLength];
        requestBuffer.readBytes(requestBytes);
        requestBuffer.release();

        //去掉10位00
        byte[] requestBody = new byte[requestLength - 10];
        System.arraycopy(requestBytes, 10, requestBody, 0, requestLength-10);

        //处理报文并接收返回的报文
        requestBytes=analysisMessage(requestBody);
        
        //将10位00补上
        requestBody = new byte[requestBytes.length+10];
        for (int i = 0; i < 10; i++) {
            requestBody[i] = 48;
        }
        System.arraycopy(requestBytes, 0, requestBody, 10, requestBytes.length);
        
        ByteBuf responseBuffer = Unpooled.copiedBuffer(requestBody); 
        context.writeAndFlush(responseBuffer);

        BytesMessage requestMessage;
        try {
			requestMessage = session.createBytesMessage();
			requestMessage.writeBytes(requestBytes);
		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}

        String correlationId = UUID.randomUUID().toString();
        requestMessage.setJMSCorrelationID(correlationId);
        requestMessage.setJMSReplyTo(responseQueue);

    	handlerContexts.put(correlationId, context);
        requestProducer.send(requestMessage);
	}

	private byte[] analysisMessage(byte[] requestBytes) {
	          ISOMsg m1 = new ISOMsg();
	          ISOMsg m2 = new ISOMsg();
	          File file = new File(pathName );
	          
	          try {
	              customPackager = new GenericPackager(new FileInputStream(file));
	              
	              //8583解包
	              System.out.println("Original Xml unpack: ");
	              m2.setPackager (customPackager);
                  m2.unpack(requestBytes);
                  System.out.println("Package Info from Receiving");             
                  logISOMsg(m2);
	              
                  System.out.println("Start to pack.......");
	              m1.set (0,"0820");
	              m1.set (3, "000000");
	              m1.set (11, "000001");
	              m1.set (13, "1234");
	              m1.set (39,"00");
	              m1.set (41, "29110001");
	              m1.set (48, "SU12340000000000000000000000003");
	              m1.set (70, "301");
	              
	              System.out.println("Package  Info for Sending");
	              logISOMsg(m1);
	              
	            //8583打包
	              m1.setPackager (customPackager);
	              byte[] binaryImage = m1.pack();
	              System.out.println("pack string=" + new String(binaryImage));
	              
	              return binaryImage;
	        } catch (ISOException e) {
	            e.printStackTrace();
	        }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        
    }
	
	public static void logISOMsg(ISOMsg msg) {
        System.out.println("----ISO MESSAGE-----");
        try {
            System.out.println("  MTI : " + msg.getMTI());
            for (int i=1;i<=msg.getMaxField();i++) {
                if (msg.hasField(i)) {
                    System.out.println("    Field-"+i+" : "+msg.getString(i));
                }
            }
        } catch (ISOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("--------------------");
        }
    }

    @Override
	public synchronized void onMessage(Message message) {
        String correlationId;
		try {
			correlationId = message.getJMSCorrelationID();
		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}

    	if (!handlerContexts.containsKey(correlationId)) {
    		return;
    	}

    	ChannelHandlerContext context = handlerContexts.remove(correlationId);

    	BytesMessage responseMessage = (BytesMessage)message;
    	int responseLength;
    	byte[] responseBytes;
		try {
			responseLength = (int) responseMessage.getBodyLength();
			responseBytes = new byte[responseLength];
			responseMessage.readBytes(responseBytes);
		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}

		ByteBuf responseBuffer = Unpooled.copiedBuffer(responseBytes); 
    	context.writeAndFlush(responseBuffer);
	}
	
}
