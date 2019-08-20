package com.throwsnew.resilience4jdemo;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-16 下午4:27 <br/>
 * Desc: 重试时先向限流器请求执行权限
 */
public class RetryWithLimiter {

    //自定义重试器
    private final RetryConfig commonConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(100))
            .retryExceptions(Throwable.class)
            .retryOnResult(Objects::isNull)
            .build();
    // 自定义限流器
    private RateLimiterConfig config = RateLimiterConfig.custom()
            //等待限流器分配执行权限的超时时间,等待时间不应该超过刷新周期
            .timeoutDuration(Duration.ofMillis(100))
            //限流器刷新周期
            .limitRefreshPeriod(Duration.ofSeconds(1))
            //限流器刷新周期运行执行次数
            .limitForPeriod(2)
            .build();

    /**
     * 用限流器控制每秒重试次数不超过2
     */
    @Test
    public void retryTwiceOneSecond() throws Throwable {
        BackendService backendService = () -> {
            throw new RuntimeException();
        };

        AtomicInteger count = new AtomicInteger(0);

        //先用限流器 装饰backendService得到limitedSupplier
        RateLimiter rateLimiter = RateLimiter.of("retryTwiceOneSecond", config);
        Supplier<String> limitedSupplier = RateLimiter
                .decorateSupplier(rateLimiter, () -> {
                    System.out.println(count.incrementAndGet());
                    throw new RuntimeException(count.get() + " times fail");
                });

        //再用重试器装饰limitedSupplier得到limitedRetrySupplier
        Retry retry = Retry.of("retryTwiceOneSecond", commonConfig);
        Supplier<String> limitedRetrySupplier = Retry.decorateSupplier(retry, limitedSupplier);

        //通过Try调用limitedRetrySupplier，并捕获异常
        Try<String> tryDoSomething = Try.ofSupplier(limitedRetrySupplier).recover(throwable -> {
            Assert.assertEquals("出错时刚好执行两次", 2, count.get());
            Assert.assertTrue("是限流器触发了异常", throwable instanceof RequestNotPermitted);
            throwable.printStackTrace();
            return null;
        });
        String result = tryDoSomething.get();
        Assert.assertNull(result);
    }

    interface BackendService {

        String doSometing();
    }

}
