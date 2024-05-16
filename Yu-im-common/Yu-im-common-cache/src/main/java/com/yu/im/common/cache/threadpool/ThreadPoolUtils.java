package com.yu.im.common.cache.threadpool;

import java.util.concurrent.*;

/**
 * @author yu
 * @description 线程工具类
 * @date 2024-05-16
 */
public class ThreadPoolUtils {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(16,
            16,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4096),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 在线程池中执行任务
     */
    public static void execute(Runnable command){
        executor.execute(command);
    }

    public static <T> Future<T> shumit(Callable<T> task){
        return executor.submit(task);
    }

    public static void shutdown(){
        if (executor != null){
            executor.shutdown();
        }
    }
}
