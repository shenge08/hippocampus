package org.dreamlife.hippocampus.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CuratorConfiguration {
    @Value("${zookeeper}")
    private String zookeeper;
    public static final Pattern IP_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");

    @Bean(initMethod="start",destroyMethod = "close")
    public CuratorFramework curatorFramework(){
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(getZookeeperName())
                .sessionTimeoutMs(1000)    // 会话超时时间
                .connectionTimeoutMs(1000) // 连接超时时间
                // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        return client;
    }

    private String getZookeeperName(){
        List<String> zk = new ArrayList<>();
        Matcher matcher = IP_PATTERN.matcher(zookeeper);
        while (matcher.find()){
            zk.add(matcher.group());
        }
        String zkServers = "";

        if(zk.size()<1){
            //throw new RuntimeException("zookeeper not findOne");
            //有可能是直接写域名
            zkServers = zookeeper.replaceAll("zookeeper://","");
        }else {

            for(String s:zk){
                zkServers=s+",";
            }
            zkServers = zkServers.substring(0,zkServers.length()-1);
        }
        return zkServers;
    }
}
