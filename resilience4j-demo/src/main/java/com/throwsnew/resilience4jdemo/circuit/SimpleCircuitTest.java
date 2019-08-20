package com.throwsnew.resilience4jdemo.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-20 下午5:38 <br/>
 * Desc: 熔断器测试
 */
public class SimpleCircuitTest {

    /**
     * 使用CircuitBreaker的默认配置
     * 让100个请求都失败，刚好满足CircuitBreaker的统计个数，触发熔断
     */
    @Test
    public void defaultCircuit() {
        CircuitBreakerConfig defaultConfig = CircuitBreakerConfig.ofDefaults();
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(defaultConfig);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("default");
        System.out.print(String.format("默认配置 \r\n 错误率 %f \r\n "
                        + "关闭状态统计请求数 %d \r\n 打开后想变成半开的等待时间 %d ms \r\n"
                        + " 标记失败请求的条件 %s \r\n 半开状态统计请求数 %d \r\n",
                defaultConfig.getFailureRateThreshold(),
                defaultConfig.getRingBufferSizeInClosedState(),
                defaultConfig.getWaitDurationInOpenState().toMillis(),
                defaultConfig.getRecordFailurePredicate().toString(),
                defaultConfig.getRingBufferSizeInHalfOpenState()));

        BackEndService backEndService = () -> {
            throw new RuntimeException();
        };

        Supplier<String> supplier = CircuitBreaker.decorateSupplier(circuitBreaker, backEndService::doSomething);
        String result = Try.ofSupplier(supplier)
                .recover(throwable -> "Hello from Recovery").get();
        Assert.assertEquals("遇到异常", "Hello from Recovery", result);
        for (int i = 0; i < 100; i++) {
            try {
                supplier.get();
            } catch (Exception ignored) {
            }
        }
        Assert.assertEquals("熔断开", "OPEN", circuitBreaker.getState().toString());
        System.out.println("熔断状态" + circuitBreaker.getState());
        System.out.println("失败个数" + circuitBreaker.getMetrics().getNumberOfFailedCalls());
        System.out.println("拒绝个数" + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
        System.out.println("ringBuffer保存的请求个数" + circuitBreaker.getMetrics().getNumberOfBufferedCalls());

    }

    @FunctionalInterface
    public interface BackEndService {

        String doSomething();
    }
}
