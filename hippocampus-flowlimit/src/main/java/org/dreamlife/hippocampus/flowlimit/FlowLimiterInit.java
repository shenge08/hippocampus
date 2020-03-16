package org.dreamlife.hippocampus.flowlimit;

import com.ptc.board.flowlimit.FlowLimiterManager;
import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.config.FlowLimiterConfig;
import com.ptc.board.flowlimit.router.DefaultLimiterFactoryImp;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 限流拦截器初始化
 *
 * @author baihua
 * @date 2018/7/10
 */
@Configuration
public class FlowLimiterInit {

    @Bean
    @ConfigurationProperties(prefix = "flowlimit")
    FlowLimiterConfig flowLimiterConfig() {
        return new FlowLimiterConfig();
    }

    @Bean
    FlowLimiterService limitService() {
        return new FlowLimiterManager().create(flowLimiterConfig(), new DefaultLimiterFactoryImp());
    }

}
