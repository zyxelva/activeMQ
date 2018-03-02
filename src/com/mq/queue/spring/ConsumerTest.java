/**
 * 
 */
package com.mq.queue.spring;

/**
 * @author KEN
 *
 */

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerTest {

	private static ApplicationContext appContext = new ClassPathXmlApplicationContext( "applicationContext.xml");

	private static void receive() {
		IConsumerService consumerService = (IConsumerService) appContext.getBean("consumerService");
		consumerService.receive();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		receive();
	}
}

