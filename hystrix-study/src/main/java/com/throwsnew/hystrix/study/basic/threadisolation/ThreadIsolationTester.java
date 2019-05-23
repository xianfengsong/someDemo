package com.throwsnew.hystrix.study.basic.threadisolation;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-5-23 下午6:24 <br/>
 * Desc: 测试使用线程隔离
 */
public class ThreadIsolationTester {

    /**
     * 测试并发调用数超过线程池大小,观察异常日志
     */
    @Test
    public void testOverCoreSize() throws InterruptedException {
        int execTimeout = 1000;
        int coreSize = 10;

        HystrixCommandProperties.Setter commandProp = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                .withExecutionTimeoutInMilliseconds(execTimeout);
        HystrixThreadPoolProperties.Setter poolProp = HystrixThreadPoolProperties.Setter()
                .withCoreSize(coreSize);
        Setter setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandConcurrent"))
                .andCommandPropertiesDefaults(commandProp)
                .andThreadPoolPropertiesDefaults(poolProp);

        ExecutorService executorService = Executors.newFixedThreadPool(coreSize * 2);
        for (int i = 0; i < coreSize + 1; i++) {
            CommandConcurrent cmd = new CommandConcurrent(setter, execTimeout - 100);
            executorService.execute(cmd::execute);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }
}
