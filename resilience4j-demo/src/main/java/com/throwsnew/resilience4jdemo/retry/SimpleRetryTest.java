package com.throwsnew.resilience4jdemo.retry;

import static io.github.resilience4j.retry.RetryConfig.DEFAULT_WAIT_DURATION;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.CheckedFunction1;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


/**
 * author Xianfeng <br/>
 * date 19-8-15 下午7:37 <br/>
 * Desc: 测试retry的用法
 */
public class SimpleRetryTest {

    private final RetryConfig commonConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(DEFAULT_WAIT_DURATION))
            .retryExceptions(Throwable.class)
            .retryOnResult(Objects::isNull)
            .build();

    /**
     * 同步重试，重试5次
     */
    @Test
    public void retryAndFail() {
        Retry retry = Retry.of("sync retry", commonConfig);
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

    /**
     * 没有触发重试 正常获得结果
     */
    @Test
    public void retryAndGetResult() {
        Retry retry = Retry.ofDefaults("retryAndGetResult");
        try {
            //这谁看得懂
            String result = retry.executeCallable(((Service) () -> "hello")::doSomething);
            Assert.assertEquals("hello", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听retry的执行
     */
    @Test
    public void retryWithListener() {
        Retry retry = Retry.of("retryWithListener", commonConfig);

        AtomicBoolean triggerOnSuccess = new AtomicBoolean(false);

        //onSuccess 只要调用被重试了最大次数，并且没抛出异常就触发，不管结果如何
        retry.getEventPublisher().onSuccess(successEvent -> {
            triggerOnSuccess.set(true);
            System.out.println(successEvent.toString());
        });
        //onError 调用被重试了最大次数，然后还是抛出了异常
        retry.getEventPublisher().onError(errorEvent -> System.out.println(errorEvent.toString()));
        //onRetry 每次重试否触发
        retry.getEventPublisher().onRetry(event -> System.out.println(event.toString()));
        //onEvent 包括所有事件
        retry.getEventPublisher().onEvent(e -> System.out.println("All\t" + e.getEventType()));

        try {
            //触发onError
//            retry.executeCallable(() -> {throw new RuntimeException();});
            //触发onSuccess
            retry.executeCallable(() -> null);
            Assert.assertTrue(triggerOnSuccess.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装一个有参数的函数和重试器到CheckedFunction1中，之后CheckedFunction1执行apply会自动重试
     */
    @Test
    public void retryFunction() {
        Retry retry = Retry.of("retryFunction", commonConfig);
        try {
            CheckedFunction1<Object, String> toStringFunction = Retry
                    .decorateCheckedFunction(retry, (CheckedFunction1<Object, String>) o -> {
                        System.out.println("apply!");
                        return o.toString().toUpperCase();
                    });
            toStringFunction.apply(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 使用异步执行调用，被调用接口始终返回异常
     * 重试5次，最后抛出异常
     *
     * @throws ExecutionException 最后一个异常
     */
    @Test(expected = ExecutionException.class)
    public void asyncRetry() throws ExecutionException {
        //创建retry
        Retry retry = Retry.of("async retry", commonConfig);
        //被retry的接口
        Service service = () -> {
            System.out.println("啊！");
            throw new RuntimeException(Thread.currentThread().getName());
        };
        //封装的CompletionStage，执行Service接口
        Supplier<CompletionStage<String>> completionStageSupplier = () -> CompletableFuture
                .supplyAsync(service::doSomething);
        //发起异步执行，使用定时线程池ScheduledExecutorService
        Supplier<CompletionStage<String>> resultSupplier = Retry
                .decorateCompletionStage(retry, Executors.newScheduledThreadPool(3),
                        completionStageSupplier);
        try {
            //获得结果(最多等待10s)
            String result = resultSupplier.get().toCompletableFuture().get(10, TimeUnit.SECONDS);
            Assert.assertNull(result);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            //service执行失败,抛出异常
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 比较retryExceptions和ignoreExceptions的作用
     * 测试 为所有异常重试，除了RuntimeException，观察输出的event类型
     */
    @Test
    public void testExceptionType() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(DEFAULT_WAIT_DURATION))
                .retryExceptions(Throwable.class)
                .ignoreExceptions(RuntimeException.class)
                .build();
        Retry retry = Retry.of("testExceptionType", config);

        retry.getEventPublisher()
                .onEvent(e -> System.out.println("事件\t" + e.getEventType() + " 异常" + e.getLastThrowable().toString()));
        //因为是RuntimeException子类，输出 IGNORED
        try {
            retry.executeCallable(() -> {
                throw new IllegalArgumentException();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        //因为是RuntimeException，输出 IGNORED
        try {
            retry.executeCallable(() -> {
                throw new RuntimeException();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Exception是Throwable的子类，输出retry
        try {
            retry.executeCallable(() -> {
                throw new Exception();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface Service {

        String doSomething();
    }
}
