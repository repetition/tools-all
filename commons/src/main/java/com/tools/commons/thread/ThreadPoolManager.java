package com.tools.commons.thread;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private static ThreadPoolManager sInstance=null;

    public static ThreadPoolManager getInstance(){
        if (null == sInstance){
            sInstance = new ThreadPoolManager();
        }
        return sInstance;
    }

    private ThreadPoolExecutor executor;
    private int corePoolSize = 20;
    private int maximumPoolSize = 30;
    private int keepAliveTime = 5;

    public ThreadPoolManager() {

            /**
             * corePoolSize:核心线程数
             * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
             * keepAliveTime：非核心线程闲置时间超时时长
             * unit：keepAliveTime的单位
             * workQueue：等待队列，存储还未执行的任务
             * threadFactory：线程创建的工厂
             * handler：异常处理机制
             *
             */
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10));
    }

    /**
     * 使用线程执行一个Runnable
     * @param run  Runnable
     */
    public void execute(Runnable run){
        executor.execute(run);
    }


    public ThreadPoolExecutor getExecutor(){
        return executor;
    }

    /**
     * 从线程池队列里面移除
     * @param run  Runnable
     */
    public void cancel(Runnable run) {
        if (run != null) {
            executor.getQueue().remove(run);//把任务移除等待队列
        }
    }


}
