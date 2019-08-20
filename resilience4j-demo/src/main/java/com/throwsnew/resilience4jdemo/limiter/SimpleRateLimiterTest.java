package com.throwsnew.resilience4jdemo.limiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-16 下午2:40 <br/>
 * Desc:
 */
public class SimpleRateLimiterTest {

    // 自定义限流器
    // 就这三个选项，有点垃圾
    private RateLimiterConfig config = RateLimiterConfig.custom()
            //等待限流器分配执行权限的超时时间,等待时间不应该超过刷新周期
            .timeoutDuration(Duration.ofMillis(100))
            //限流器刷新周期
            .limitRefreshPeriod(Duration.ofSeconds(1))
            //限流器刷新周期内允许的最大执行次数
            .limitForPeriod(1)
            .build();
    private RateLimiter rateLimiter = RateLimiter.of("testConcurrent", config);

    /**
     * 配置限流为 1次/秒 第二次执行会失败
     */
    @Test
    public void testFail() {

        // Create a RateLimiter
        RateLimiter rateLimiter = RateLimiter.of("backendName", config);
        // Decorate your call to BackendService.doSomething()
        Supplier<String> restrictedSupplier = RateLimiter
                .decorateSupplier(rateLimiter, () -> new BackendService() {
                }.doSomthing());

        // First call is successful
        Try<String> firstTry = Try.ofSupplier(restrictedSupplier);
        Assert.assertTrue(firstTry.isSuccess());

        // Second call fails, because the call was not permitted
        Try<String> secondTry = Try.ofSupplier(restrictedSupplier);
        Assert.assertTrue(secondTry.isFailure());
        Assert.assertTrue(secondTry.getCause() instanceof RequestNotPermitted);
    }

    /**
     * 两个线程使用一个rateLimiter 正常限流，只有一个能成功
     */
    @Test
    public void testConcurrent() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Callable<String>> callables = new ArrayList<>();
        callables.add(() -> {
            Try<String> firstTry = Try.ofCallable(RateLimiter.decorateCallable(rateLimiter, () -> new BackendService() {
            }.doSomthing()));
            System.out.println("firstTry?" + firstTry.isSuccess());
            return firstTry.get();
        });
        callables.add(() -> {
            Try<String> secondTry = Try
                    .ofCallable(RateLimiter.decorateCallable(rateLimiter, () -> new BackendService() {
                    }.doSomthing()));
            System.out.println("secondTry?" + secondTry.isSuccess());
            return secondTry.get();
        });
        executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }

    interface BackendService {

        default String doSomthing() {
            return "hello";
        }
    }

}
