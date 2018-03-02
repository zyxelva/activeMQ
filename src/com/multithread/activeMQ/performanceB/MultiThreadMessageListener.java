package com.multithread.activeMQ.performanceB;


import java.util.concurrent.ExecutorService;

import javax.jms.Message;
import javax.jms.MessageListener;


/**
 * ��Ϣ��������ʹ�õĶ��߳���Ϣ��������
 * @author linwei
 *
 */
public class MultiThreadMessageListener implements MessageListener {

    //Ĭ���̳߳�����
    public final static int DEFAULT_HANDLE_THREAD_POOL=10;
    //���Ĵ����߳���.
    private int maxHandleThreads;
    //�ṩ��Ϣ�ص����ýӿ�
    private MessageHandler messageHandler;

    private ExecutorService handleThreadPool;
    
    
    public MultiThreadMessageListener(MessageHandler messageHandler){
        this(DEFAULT_HANDLE_THREAD_POOL, messageHandler);
    }
    
    public MultiThreadMessageListener(int maxHandleThreads,MessageHandler messageHandler){
        this.maxHandleThreads=maxHandleThreads;
        this.messageHandler=messageHandler;
        //֧�������Ĺ̶���С���̳߳�(�����ֶ�������)
        this.handleThreadPool = new FixedAndBlockedThreadPoolExecutor(this.maxHandleThreads);
    }
    
    
    /**
     * �����������Զ����õķ���
     */
    @Override
    public void onMessage(final Message message) {
        //ʹ��֧�������Ĺ̶���С���̳߳���ִ�в���
        this.handleThreadPool.execute(new Runnable() {
            public void run() {
                try {
                    MultiThreadMessageListener.this.messageHandler.handle(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
