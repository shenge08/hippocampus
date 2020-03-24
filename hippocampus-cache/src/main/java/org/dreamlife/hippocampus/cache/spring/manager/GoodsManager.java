package org.dreamlife.hippocampus.cache.spring.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/19
 */
@Component
@Slf4j
public class GoodsManager {
    @Resource
    private Cache goodsCache;


    private AtomicLong times = new AtomicLong();

    /**
     * #p0 表示参数列表中偏移量为0的元素
     * @param id
     * @return
     */
    public String load(Long id) {
        String value = goodsCache.get(id, String.class);
        if(value == null){
            // load cache
            log.info("cache miss value:{}",id);
            value = loadFromDB(id);
            goodsCache.put(""+id,value);
        }
        return value;
    }

    public void delete(Long id){
        log.info("evict cache {}",id);
        goodsCache.evict(id);
    }

    public String update(Long id){
        String s = loadFromDB(id);
        goodsCache.evict(id);
        return s;
    }

    public void deleteAll(){
        log.info("evict all cache");
        goodsCache.clear();
    }


    private String loadFromDB(Long id){
        return String.format("{\"id\":%s,\"times\":%s}", id, times.incrementAndGet());
    }
}
