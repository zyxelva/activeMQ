package com.mq.topic.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PublisherTest {

    private static ApplicationContext appContext = new ClassPathXmlApplicationContext( "applicationContext.xml");

    private static void send() {
        IPublisherService publisherService = (IPublisherService) appContext.getBean("publisherService");
        publisherService.send();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        send();
    }
}
