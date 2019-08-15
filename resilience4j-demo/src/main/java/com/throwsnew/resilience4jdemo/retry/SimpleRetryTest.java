package com.throwsnew.resilience4jdemo.retry;

import static io.github.resilience4j.retry.RetryConfig.DEFAULT_WAIT_DURATION;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-15 下午7:37 <br/>
 * Desc:
 */
public class SimpleRetryTest {

    @Test
    public void retryAndFail() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofMillis(DEFAULT_WAIT_DURATION))
                .retryExceptions(Throwable.class)
                .retryOnResult(Objects::isNull)
                .build();
        Retry retry = Retry.of("sync retry", config);
        AtomicInteger times = new AtomicInteger();
        try {
            retry.executeCallable(() -> {
                times.getAndIncrement();
                throw new RuntimeException(String.valueOf(times.get()));
            });
        } catch (Exception e) {
            //重试仍然失败,抛出最后一次重试的异常
            e.printStackTrace();
        }
        Assert.assertEquals("重试5次", 5, times.get());
    }

}
