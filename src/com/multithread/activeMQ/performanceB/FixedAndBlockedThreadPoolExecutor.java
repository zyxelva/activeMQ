package com.multithread.activeMQ.performanceB;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * ֧�������Ĺ̶���С���̳߳�
 * @author linwei
 *
 */
public class FixedAndBlockedThreadPoolExecutor extends ThreadPoolExecutor {

    
    //һ��������Ļ����� Lock����������ʹ�� synchronized ��������������ʵ���ʽ����������ͬ��һЩ������Ϊ�����壬�����ܸ�ǿ��
    //ʹ�� lock �������� try����֮ǰ/֮��Ĺ�����
    private ReentrantLock lock = new ReentrantLock();
    
    private Condition condition = this.lock.newCondition();
    
    public FixedAndBlockedThreadPoolExecutor(int size) {
        super(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    
    /**
     * ���̳߳���û�п����߳�ʱ,�����˷����ĵ����߳�.ֱ���̳߳������߳��п����߳�.
     */
    @Override
    public void execute(Runnable command) {
        //����ͬ������
        this.lock.lock();
        super.execute(command);
        try {
            //����̳߳ص������Ѿ��ﵽ����̳߳ص�����,����й������
            if (getPoolSize() == getMaximumPoolSize()) {
                this.condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            this.lock.lock();
            this.condition.signal();
        } finally {
            this.lock.unlock();
        }
    }
    
    
}
