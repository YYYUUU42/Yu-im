package com.yu.im.common.cache.model;

import com.yu.im.common.cache.model.base.IMCommonCache;

/**
 * @author yu
 * @description 业务数据缓存
 * @date 2024-05-16
 */
public class IMBusinessCache<T> extends IMCommonCache {

    private T data;

    public IMBusinessCache<T> with(T data){
        this.data = data;
        this.exist = true;
        return this;
    }

    public IMBusinessCache<T> withVersion(Long version){
        this.version = version;
        return this;
    }

    public IMBusinessCache<T> retryLater(){
        this.retryLater = true;
        return this;
    }

    public IMBusinessCache<T> notExist(){
        this.exist = false;
        this.version = -1L;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
