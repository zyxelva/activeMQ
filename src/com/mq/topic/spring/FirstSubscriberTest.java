package com.mq.topic.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FirstSubscriberTest {

    private static ApplicationContext appContext = new ClassPathXmlApplicationContext( "applicationContext.xml");

    private static void receive() {
        ISubscriberService firstSubscriberService = (ISubscriberService) appContext.getBean("firstSubscriberService");
        firstSubscriberService.receive();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        receive();
    }
}
