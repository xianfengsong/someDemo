package com.throwsnew.hystrix.study.basic.command;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;

/**
 * author Xianfeng <br/>
 * date 18-10-10 下午4:38 <br/>
 * Desc:
 */
public class CommandHelloWorld extends HystrixCommand<String> {

    private final String name;
    private final boolean useFallback;

    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"));
        this.name = name;
        this.useFallback = false;
    }

    public CommandHelloWorld(String name, boolean useFallback) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"));
        this.name = name;
        this.useFallback = useFallback;
    }

    public CommandHelloWorld() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("commandHelloWorld"))
                .andCommandPropertiesDefaults(HystrixPropertiesCommandDefault.Setter())
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("helloWorldPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()));
        this.useFallback = false;
        this.name = "default";
    }

    @Override
    protected String run() throws Exception {
        if (useFallback) {
            throw new RuntimeException("exec fail");
        } else {
            return "Hello " + name + "!";
        }
    }

    @Override
    protected String getFallback() {
        return "Fallback result";
    }
}
