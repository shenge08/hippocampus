package org.dreamlife.hippocampus.performance.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Slf4j
@RestController
public class RandomApi {

    @GetMapping("/ping/{randomSuffix}")
    public String ping(@PathVariable("randomSuffix") String randomSuffix){
        int sleep = new Random().nextInt(100);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String flag = String.format("invoke %s, cost time: %s ms","/ping/"+randomSuffix,sleep);
        log.info(flag);
        return flag;
    }
}
