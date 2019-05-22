package com.throwsnew.hystrix.study.basic.configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author Xianfeng <br/>
 * date 18-10-19 下午7:06 <br/>
 * Desc: 用继承并通过代码自定义配置的hystrix
 */
public class CommandConfigByCode extends HystrixCommand<String> {

    private AtomicInteger runTimeInMilliseconds = new AtomicInteger(100);
    private Boolean shouldSuccess;

    public CommandConfigByCode(Setter setter, Integer runTime, Boolean shouldSuccess) {
        super(setter);
        this.runTimeInMilliseconds.getAndSet(runTime);
        this.shouldSuccess = shouldSuccess;
    }

    /**
     * 创建一个配置对象
     *
     * @param strategy 执行请求时用什么隔离策略
     * @param requestVolumeThreshold 触发熔断的窗口中请求数上限
     * @param statisticalWindowTime 统计窗口的持续时间ms（how long Hystrix keeps metrics for the circuit breaker to use and for publishing）
     * @param errorThresholdPercentage 触发熔断的失败比例
     * @param poolSize 执行请求的线程池大小
     * @param timeout 请求执行的超时时间 ms
     * @param sleepWindow 触发熔断后 经过多久再次判断是否关闭熔断
     */
    public static Setter setter(ExecutionIsolationStrategy strategy, int requestVolumeThreshold,
            int statisticalWindowTime,
            int errorThresholdPercentage, int poolSize, int timeout, int sleepWindow) {

        HystrixCommandProperties.Setter commandProp = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(strategy)
//                .withExecutionIsolationThreadInterruptOnFutureCancel(true)
//                .withExecutionIsolationThreadInterruptOnTimeout(true)
                .withExecutionTimeoutEnabled(true)
                .withExecutionTimeoutInMilliseconds(timeout)
                // 熔断条件
                .withCircuitBreakerRequestVolumeThreshold(requestVolumeThreshold)
                .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                // 重试窗口
                .withCircuitBreakerSleepWindowInMilliseconds(sleepWindow)
                .withMetricsRollingStatisticalWindowInMilliseconds(statisticalWindowTime)
                .withMetricsRollingPercentileWindowBuckets(10);

        HystrixThreadPoolProperties.Setter poolProp = HystrixThreadPoolProperties.Setter()
                .withCoreSize(poolSize).withMaxQueueSize(10);

        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("group"))
                .andCommandPropertiesDefaults(commandProp)
                .andCommandKey(HystrixCommandKey.Factory.asKey("keyTest"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("pool"))
                .andThreadPoolPropertiesDefaults(poolProp);
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(runTimeInMilliseconds.get());
        if (shouldSuccess) {
            return "hello world";
        } else {
            throw new Exception("should fail");
        }
    }

    @Override
    protected String getFallback() {
        return "fallback";
    }

}
