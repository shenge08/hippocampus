package org.dreamlife.hippocampus.cache.spring;

import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.cache.spring.manager.GoodsManager;
import org.dreamlife.hippocampus.cache.spring.manager.UserManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @birthday 11-22
 * @date 2020/3/18
 */
@Slf4j
@EnableCaching
@SpringBootApplication
public class RedisCacheMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisCacheMain.class);
        testAboutInstance(context);
        System.exit(0);
    }

    /**
     * 相比较而言，直接使用Cache要比使用注解方便很多
     * @param context
     */
    public static void testAboutInstance(ConfigurableApplicationContext context){
        GoodsManager userManager = context.getBean(GoodsManager.class);
        String value = userManager.load(17L);
        log.info("load once,,,{}",value);
        value =  userManager.load(17L);
        log.info("load once,,,{}",value);
        
        value =  userManager.load(17L);
        log.info("load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("load once,,,{}",value);

        userManager.delete(17L);
        value =  userManager.load(17L);
        log.info("After evict 17, load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("After evict 17，load once,,,{}",value);

        userManager.deleteAll();
        value =  userManager.load(17L);
        log.info("After evict all, load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("After evict all，load once,,,{}",value);

    }
    public static void testAboutAnnocation(ConfigurableApplicationContext context){
        UserManager userManager = context.getBean(UserManager.class);
        String value = userManager.load(17L);
        log.info("load once,,,{}",value);
        value =  userManager.load(17L);
        log.info("load once,,,{}",value);
        value =  userManager.load(17L);
        log.info("load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("load once,,,{}",value);

        userManager.delete(17L);
        value =  userManager.load(17L);
        log.info("After evict 17, load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("After evict 17，load once,,,{}",value);

        userManager.deleteAll();
        value =  userManager.load(17L);
        log.info("After evict all, load once,,,{}",value);
        value =  userManager.load(16L);
        log.info("After evict all，load once,,,{}",value);
    }
}
