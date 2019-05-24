package com.throwsnew.hystrix.study.basic.request;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-5-24 下午2:11 <br/>
 * Desc: 测试请求缓存的作用
 */
public class RequestCacheTest {

    /**
     * 在同一context中，如果cache key相同，命令再次执行会使用缓存
     */
    @Test
    public void testCacheSameContext() {
        CommandUsingRequestCache cmd1 = new CommandUsingRequestCache(1);
        CommandUsingRequestCache cmd1_1 = new CommandUsingRequestCache(1);
        CommandUsingRequestCache cmd2 = new CommandUsingRequestCache(2);

        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Assert.assertFalse(cmd1.execute());
            Assert.assertFalse(cmd1.isResponseFromCache());
            cmd1_1.execute();
            Assert.assertTrue(cmd1_1.isResponseFromCache());
            cmd2.execute();
            Assert.assertFalse(cmd2.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }

    /**
     * 在不同context中，缓存会重新计算
     */
    @Test
    public void testCacheDifferentContext() {
        CommandUsingRequestCache cmd1a = new CommandUsingRequestCache(1);
        CommandUsingRequestCache cmd1b = new CommandUsingRequestCache(1);

        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            cmd1a.execute();
            Assert.assertFalse("没有cache", cmd1a.isResponseFromCache());
            cmd1b.execute();
            Assert.assertTrue("返回cache", cmd1b.isResponseFromCache());
        } finally {
            context.shutdown();
        }
        //重新初始化context
        context = HystrixRequestContext.initializeContext();
        CommandUsingRequestCache cmd1c = new CommandUsingRequestCache(1);
        CommandUsingRequestCache cmd1d = new CommandUsingRequestCache(1);

        try {

            cmd1c.execute();
            Assert.assertFalse("没有cache", cmd1c.isResponseFromCache());
            cmd1d.execute();
            Assert.assertTrue("返回cache", cmd1d.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }

    public class CommandUsingRequestCache extends HystrixCommand<Boolean> {

        private final int value;

        protected CommandUsingRequestCache(int value) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.value = value;
        }

        @Override
        protected Boolean run() {
            return value == 0 || value % 2 == 0;
        }

        @Override
        protected String getCacheKey() {
            return String.valueOf(value);
        }
    }
}
