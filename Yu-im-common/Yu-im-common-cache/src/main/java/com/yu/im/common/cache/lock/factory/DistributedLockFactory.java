package com.yu.im.common.cache.lock.factory;

import com.yu.im.common.cache.lock.DistributedLock;

/**
 * @author yu
 * @description 分布式锁工程接口
 * @date 2024-05-16
 */
public interface DistributedLockFactory {

    /**
     * 根据key获取分布式锁
     */
    DistributedLock getDistributedLock(String key);
}
