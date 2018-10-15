package com.mmall.concurrency.annoations;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 并发测试类
 * @author DUSTY
 */
@Slf4j
@NotThreadSafe
public class ConcurrencyTest {

    /**
     * 请求总数
     */
    public static int clientTotal = 5000;

    /**
     * 同时并发执行的线程数
     */
    public static int threadTotal = 200;

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        //定义一个线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //定义信号量，允许的并发数threadTotal
        final Semaphore semaphore = new Semaphore(threadTotal);
        //定义倒计时锁,我们希望所有请求执行完之后才统计计数的结果，所以传入clientTotal
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        //开始放入请求
        for (int i = 0;i<clientTotal;i++) {
            //把请求放入线程池
            executorService.execute(() ->{
                try {
                    //判断当前线程是否运行被执行
                    semaphore.acquire();
                    add();
                    //释放当前线程
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception",e);
                }
                //每执行一次，clientTotal -1
                countDownLatch.countDown();
            });
        }
        //Be sure that the current count equals zero
        countDownLatch.await();
        //不再使用线程池，则关闭
        executorService.shutdown();
        log.info("count:{}",count);
    }

    /**
     * 计数方法
     */
    private static void add() {

        count++;
    }
}
