package org.dreamlife.hippocampus.zookeeper.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class MasterAcquireHandler implements InitializingBean {
    private final String path = "/user/gamma/master";
    @Autowired
    private CuratorFramework client;

    private LeaderLatch leaderLatch;

    public MasterAcquireHandler(){

    }
    public MasterAcquireHandler(CuratorFramework client){
        this.client=client;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        setLeaderLatch(this.path);
    }

    public void setLeaderLatch(String path) {
        try {
            String id = "client#" + UUID.randomUUID().toString();
            log.info("抢主竞争：当前实例id：{}",id);
            leaderLatch = new LeaderLatch(client, path, id);
            LeaderLatchListener leaderLatchListener = new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    log.info("当前实例竞选为主节点, id={}", leaderLatch.getId());
                }

                @Override
                public void notLeader() {
                    log.info("当前实例竞选失败, id={}", leaderLatch.getId());
                }
            };
            leaderLatch.addListener(leaderLatchListener);
            leaderLatch.start();
        } catch (Exception e) {
            log.error("创建LeaderLatch失败, path={}", path);
        }
    }

    public boolean isSlave() {
        return !isMaster();
    }
    public boolean isMaster() {
        return leaderLatch.hasLeadership();
    }

}
