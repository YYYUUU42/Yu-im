package com.yu.im.common.cache.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author yu
 * @description 分布式锁接口
 * @date 2024-05-16
 */
public interface DistributedLock {

    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    boolean tryLock(long waitTime,  TimeUnit unit) throws InterruptedException;

    boolean tryLock() throws InterruptedException;

    void lock(long leaseTime, TimeUnit unit);

    void unlock();

    boolean isLocked();

    boolean isHeldByThread(long threadId);

    boolean isHeldByCurrentThread();
}
