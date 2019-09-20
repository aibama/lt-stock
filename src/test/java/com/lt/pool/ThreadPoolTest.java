package com.lt.pool;

import java.util.concurrent.*;

/**
 * @author gaijf
 * @description
 * @date 2019/9/20
 */
public class ThreadPoolTest {

    public static class MyTask implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException{
        MyTask task = new MyTask();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 8, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        long task = executor.getTaskCount()-executor.getCompletedTaskCount();
                        if (task >= 25 && executor.getPoolSize() < 2){
                            executor.setCorePoolSize(executor.getCorePoolSize()+2);
                        }else if (task >= 50 && executor.getPoolSize() < 4){
                            executor.setCorePoolSize(executor.getCorePoolSize()+2);
                        }
                    }
                }
        );
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            executor.submit(task);
            Thread.sleep(10);
            if (executor.getPoolSize() == 8){
                Thread.sleep(3000);
            }
            long count = executor.getTaskCount()-executor.getCompletedTaskCount();
            System.out.println("RealPriceTask:总数:"+executor.getTaskCount()+"完成:"+executor.getCompletedTaskCount()+"等待:"+count+"线程数量:"+executor.getPoolSize());

        }
    }
}
