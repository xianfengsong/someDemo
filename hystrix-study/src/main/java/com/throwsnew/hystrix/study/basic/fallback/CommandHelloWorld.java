package com.throwsnew.hystrix.study.basic.fallback;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * author Xianfeng <br/>
 * date 18-10-10 下午4:38 <br/>
 * Desc: hystrix fallback逻辑相关的demo
 */
public class CommandHelloWorld extends HystrixCommand<String> {

    /**
     * 是否抛出异常，触发fallback
     */
    private final boolean useFallback;

    /**
     * 是否为badRequest，如果是抛出HystrixBadRequestException，不触发fallback
     * 抛出HystrixBadRequestException可以自定义哪些情况需要返回异常，而不是fallback
     */
    private final boolean badRequest;

    /**
     * fallback是否通过网络请求完成
     */
    private final boolean fallbackViaNetwork;


    CommandHelloWorld(boolean useFallback) {
        this(useFallback, false);
    }

    CommandHelloWorld(boolean useFallback, boolean badRequest) {
        this(useFallback, badRequest, false);
    }

    CommandHelloWorld(boolean useFallback, boolean badRequest, boolean fallbackViaNetwork) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("commandHelloWorld"))
                .andCommandPropertiesDefaults(HystrixPropertiesCommandDefault.Setter())
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("helloWorldPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()));
        this.useFallback = useFallback;
        this.badRequest = badRequest;
        this.fallbackViaNetwork = fallbackViaNetwork;
    }

    @Override
    protected String run() {
        if (useFallback) {
            if (badRequest) {
                IllegalArgumentException argumentException = new IllegalArgumentException("参数异常");
                throw new HystrixBadRequestException("badRequset", argumentException);
            } else {
                throw new RuntimeException("exec fail");
            }
        } else {
            return "HelloWorld";
        }
    }

    @Override
    protected String getFallback() {
        if (fallbackViaNetwork) {
            return new FallbackViaNetwork(0).execute();
        } else {
            return "fallback";
        }
    }

    /**
     * 如果通过网络请求实现fallback(比如查redis),需要创建一个新的command完成
     * 并且要使用和上游command不同的线程池，避免受上游影响。
     * 如果这个command也失败，可以直接返回null
     */
    private static class FallbackViaNetwork extends HystrixCommand<String> {

        private final int id;
        //当做缓存用
        private Map<Integer, String> MemCacheClient = new HashMap<>();

        public FallbackViaNetwork(int id) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorldFallbackCommand"))
                    // use a different threadpool for the fallback command
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("RemoteServiceXFallback")));
            this.id = id;
        }

        @Override
        protected String run() {
            return Optional.ofNullable(MemCacheClient.get(id)).orElse("cache");
        }

        @Override
        protected String getFallback() {
            // the fallback also failed
            // so this fallback-of-a-fallback will
            // fail silently and return null
            return null;
        }
    }

}
