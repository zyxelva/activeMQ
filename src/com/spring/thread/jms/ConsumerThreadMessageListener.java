package com.spring.thread.jms;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

@Component("consumerThreadMessageListener")
public class ConsumerThreadMessageListener implements MessageListener {
    @Override
    public void onMessage(final Message message) {

                try {
                    System.out.println("接收线程"+Thread.currentThread().getName()+"接收消息:"+((TextMessage)message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }

}