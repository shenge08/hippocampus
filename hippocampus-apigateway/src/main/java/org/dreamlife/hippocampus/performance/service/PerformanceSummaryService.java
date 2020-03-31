package org.dreamlife.hippocampus.performance.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dreamlife.hippocampus.performance.model.PerformanceRecord;
import org.dreamlife.hippocampus.performance.model.PerformanceSummary;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 性能统计服务
 *
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/3/30
 */
@Slf4j
public class PerformanceSummaryService implements InitializingBean {
    private static volatile PerformanceSummaryService INSTANCE;
    /**
     * 并行度
     */
    private final int concurrencyLevel;
    /**
     * 容器组，一个线程服务对应一个容器
     */
    private final List<Map<String, PerformanceSummary>> performanceSummaries;
    /**
     * 线程服务组
     */
    private final List<ExecutorService> services;

    public PerformanceSummaryService(int concurrencyLevel) {
        // 设置并发度
        this.concurrencyLevel = concurrencyLevel;
        // 为了使对同一个uri的操作可以只让一个线程来完成，避免消费者线程之间的并发冲突，于是在此设置线程服务组
        services = new ArrayList<>(concurrencyLevel);
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        (offset) -> {
                            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                                    1, 1,
                                    0, TimeUnit.MILLISECONDS
                                    , Queues.newLinkedBlockingQueue(1024)
                                    , new ThreadFactoryBuilder().setNameFormat(String.format("performance-summary-%s", offset)).build());
                            services.add(executor);
                        }
                );
        // 一个线程服务对应一个容器
        performanceSummaries = new ArrayList<>(concurrencyLevel);
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        (offset) -> {
                            Map<String, PerformanceSummary> segment = Maps.newHashMap();
                            performanceSummaries.add(segment);
                        }
                );
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    private int serviceOffset(String key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode()) % concurrencyLevel;
    }

    /**
     * 当调用方没有注入Spring IoC容器中的PerformanceSummaryService实例
     * 需要调用该兼容性的静态方法才能提交任务
     * @param record
     */
    public static void compatibleSubmit(PerformanceRecord record){
        INSTANCE.submit(record);
    }

    public void submit(PerformanceRecord record) {
        int offset = serviceOffset(record.getApi());
        services.get(offset).submit(
                () -> {
                    final String api = record.getApi();
                    final long responseMills = record.getResponseMills();
                    // 简单累加性能值
                    PerformanceSummary reference = performanceSummaries.get(offset).get(api);
                    if (reference == null) {
                        reference = new PerformanceSummary();
                        performanceSummaries.get(offset).put(api, reference);
                    }
                    reference.setTotalInvokeCount(reference.getTotalInvokeCount() + 1);
                    reference.setTotalResponseTime(reference.getTotalResponseTime() + responseMills);
                }
        );
    }

    /**
     * 持久化性能指标
     */
    public void sink() {
        IntStream.range(0, concurrencyLevel)
                .forEach(
                        i -> {
                            final int offset = i;
                            Runnable sink = () -> {
                                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                performanceSummaries.get(offset).keySet().stream()
                                        .filter(api -> serviceOffset(api) == offset)// 当前线程服务只处理跟自己有关的api
                                        .forEach(
                                                api -> {
                                                    // 获取性能值，并执行sink操作
                                                    PerformanceSummary value = performanceSummaries.get(offset).get(api);
                                                    long totalInvokeCount = value.getTotalInvokeCount();
                                                    if (totalInvokeCount <= 0) {
                                                        return;
                                                    }
                                                    double averageTimeCost = value.getTotalResponseTime() / totalInvokeCount;
                                                    // 打印出每个被请求接口的平均响应时间
                                                    log.info("API: {}, averageCostTime: {} ms, totalInvokeCount: {}, during {}, {}",
                                                            api, averageTimeCost, totalInvokeCount, value.getLastSinkTime(),currentTime);
                                                    // 性能值清空
                                                    performanceSummaries.get(offset).put(api,
                                                            value.setTotalResponseTime(0)
                                                                    .setTotalInvokeCount(0))
                                                                    .setLastSinkTime(currentTime);
                                                    ;
                                                }
                                        );
                            };
                            // 给每个线程服务都提交一个sink任务
                            services.get(offset).submit(sink);
                        }
                );
    }


}
