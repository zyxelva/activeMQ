package com.mq.topic.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecondSubscriberTest {

    private static ApplicationContext appContext = new ClassPathXmlApplicationContext( "applicationContext.xml");

    private static void receive() {
        ISubscriberService secondSubscriberService = (ISubscriberService) appContext.getBean("secondSubscriberService");
        secondSubscriberService.receive();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        receive();
    }
}
