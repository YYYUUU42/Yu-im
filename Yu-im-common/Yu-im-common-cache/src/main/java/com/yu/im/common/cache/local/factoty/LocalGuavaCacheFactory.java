package com.yu.im.common.cache.local.factoty;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author yu
 * @description 基于Guava的本地缓存工厂类
 * @date 2024-05-16
 */
public class LocalGuavaCacheFactory {

    public static <K, V> Cache<K, V> getLocalCache(){
        return CacheBuilder.newBuilder().initialCapacity(200).concurrencyLevel(5).expireAfterWrite(300, TimeUnit.SECONDS).build();
    }

    public static <K, V> Cache<K, V> getLocalCache(long duration){
        return CacheBuilder.newBuilder().initialCapacity(200).concurrencyLevel(5).expireAfterWrite(duration, TimeUnit.SECONDS).build();
    }

    public static <K, V> Cache<K, V> getLocalCache(int initialCapacity, long duration){
        return CacheBuilder.newBuilder().initialCapacity(initialCapacity).concurrencyLevel(5).expireAfterWrite(duration, TimeUnit.SECONDS).build();
    }
}
