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

public class ProducerTest {

	private static ApplicationContext appContext = new ClassPathXmlApplicationContext( "applicationContext.xml");

	private static void send() {
		IProducerService producerService = (IProducerService) appContext.getBean("producerService");
		producerService.send();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		send();
	}
}

