package com.spring.thread.jms;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class JmsThreadTest {

    private static ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");

    private static void send() {
        final MessageThreadService ms = (MessageThreadService) (appContext.getBean("messageThreadService"));
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 4; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(new Random().nextInt(3) * 500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + "处理发送消息" + i);
                        ms.sendMessage("你好:" + Thread.currentThread().getName() + "的消息" + i);
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        send();
    }
}
