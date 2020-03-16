package org.dreamlife.hippocampus.cache.guava.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/16
 */
public class GuavaCacheExample {
    private LoadingCache<String, String> resultCache = CacheBuilder.newBuilder()
            .recordStats()// 打开性能监控
            .maximumSize(200000)
            .expireAfterAccess(5, TimeUnit.MINUTES)// 缓存失效时间为5分钟
            .build(
                    new CacheLoader<String, String>() {
                        public String load(String key) throws IOException {
                            return key;
                        }
                    });

    /**
     * 加载数据时经过缓存
     * @param key
     * @return
     */
    public String load(String key){
        try {
            return resultCache.get(key);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取缓存命中相关指标
     * @return
     */
    public String getCacheStats() {
        return resultCache.stats().toString();
    }
}
