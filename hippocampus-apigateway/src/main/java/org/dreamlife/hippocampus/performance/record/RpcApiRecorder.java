package org.dreamlife.hippocampus.performance.record;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.performance.model.PerformanceRecord;
import org.dreamlife.hippocampus.performance.service.PerformanceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 采集rpc接口调用时长
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Activate(group = { Constants.CONSUMER })
@Slf4j
public class RpcApiRecorder implements Filter {
    @Autowired
    private PerformanceSummaryService performanceSummaryService;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long time = System.currentTimeMillis();
        String methodName = invocation.getMethodName();
        try{
            return invoker.invoke(invocation);
        }finally {
            long cost = System.currentTimeMillis() - time;
            performanceSummaryService.submit(new PerformanceRecord().setResponseMills(cost).setUri(methodName));
        }
    }
}
