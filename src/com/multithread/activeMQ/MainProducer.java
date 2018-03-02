package com.multithread.activeMQ;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 多线程发送消息
 */

public class MainProducer {
      public static void main(String[] args) {
    
          ExecutorService threadPool=Executors.newFixedThreadPool(3);
          for (int i = 0; i < 5; i++) {
              threadPool.submit(new Runnable() {
    
                @Override
                public void run() {
                    JMSProducerThread jph=new JMSProducerThread();
                    try {
                        Thread.sleep(new Random().nextInt(5)*500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jph.init();
                    jph.produce();                      
                }
            });
    
        }
         /* 
        for (int i = 0; i <5; i++) {
    
           new Thread(new Runnable() {
    
            @Override
            public void run() {
                JMSProducerThread js=new JMSProducerThread();
                try {
                    Thread.sleep(new Random().nextInt(5)*500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                js.init();
                js.produce();
    
            }
        }).start();
    
        }*/         
        }
}

