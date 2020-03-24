package org.dreamlife.hippocampus.cache.spring.config;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/22
 */
public class CacheKeyGeneratorWrapper implements Cache {
    private Cache proxy;
    private String cacheNamePrefix;

    public CacheKeyGeneratorWrapper(Cache proxy, String cacheNamePrefix){
        this.proxy=proxy;
        this.cacheNamePrefix = cacheNamePrefix;
    }

    @Override
    public String getName() {
        return proxy.getName();
    }

    @Override
    public Object getNativeCache() {
        return proxy.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object o) {
        return proxy.get(cacheNamePrefix+o);
    }

    @Override
    public <T> T get(Object o, Class<T> aClass) {
        return proxy.get(cacheNamePrefix+o,aClass);
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        return proxy.get(cacheNamePrefix+o,callable);
    }

    @Override
    public void put(Object o, Object o1) {
        proxy.put(cacheNamePrefix+o,o1);
    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        return proxy.putIfAbsent(cacheNamePrefix+o,o1);
    }

    @Override
    public void evict(Object o) {
        proxy.evict(cacheNamePrefix+o);
    }

    @Override
    public void clear() {
        proxy.clear();
    }
}
