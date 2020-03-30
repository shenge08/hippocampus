package org.dreamlife.hippocampus.application.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/18
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间默认为5分钟
        cacheManager.setDefaultExpiration(300);
        return cacheManager;
    }

    @Bean("goodsCache")
    public Cache goodsCache(CacheManager cacheManager){
        return keyPrefixWrapperCache(cacheManager.getCache("goods"),"cache:goods:");
    }

    // 添加统一KEY前缀的缓存装饰
    private Cache keyPrefixWrapperCache(Cache cache,String keyPrefix){
        return new KeyPrefixCacheWrapper(cache,keyPrefix);
    }

}
