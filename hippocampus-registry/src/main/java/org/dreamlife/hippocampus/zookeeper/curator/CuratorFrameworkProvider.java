package org.dreamlife.hippocampus.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuratorFrameworkProvider implements InitializingBean {
    public static volatile CuratorFrameworkProvider INSTANCE;
    @Autowired
    private CuratorFramework client;

    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    public static CuratorFramework get(){
        return INSTANCE.client;
    }
}
