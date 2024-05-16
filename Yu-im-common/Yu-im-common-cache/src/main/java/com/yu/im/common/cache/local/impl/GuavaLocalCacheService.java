package com.yu.im.common.cache.local.impl;

import com.google.common.cache.Cache;
import com.yu.im.common.cache.local.LocalCacheService;
import com.yu.im.common.cache.local.factoty.LocalGuavaCacheFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author yu
 * @description 基于Guava实现的本地缓存
 * @date 2024-05-16
 */
@Component
@ConditionalOnProperty(name = "cache.type.local", havingValue = "guava")
public class GuavaLocalCacheService<K, V> implements LocalCacheService<K, V> {
    //本地缓存，基于Guava实现
    private final Cache<K, V> cache = LocalGuavaCacheFactory.getLocalCache();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }
}
