package org.dreamlife.hippocampus.application.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/18
 */
@Component
@CacheConfig(cacheNames = "user")
@Slf4j
public class UserManager {
    private LongAdder times = new LongAdder();

    /**
     * #p0 表示参数列表中偏移量为0的元素
     * @param id
     * @return
     */
    @Cacheable(key ="#p0")
    public String load(Long id) {
        log.info("cache miss +1,key :{}", id);
        times.increment();
        return String.format("{\"id\":%s,\"times\":%s}", id, times.intValue());
    }

    @CacheEvict(key="'#p0")
    public void delete(Long id){
        log.info("evict cache {}",id);
    }

    @CachePut(key="'id:'+#p0")
    public String update(Long id){
        return load(id);
    }

    @CacheEvict(allEntries = true)
    public void deleteAll(){
        log.info("evict all cache");
    }


}
