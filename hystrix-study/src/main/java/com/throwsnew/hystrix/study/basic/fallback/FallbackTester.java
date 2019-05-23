package com.throwsnew.hystrix.study.basic.fallback;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-5-21 下午3:37 <br/>
 * Desc:
 */
public class FallbackTester {

    /**
     * 测试execute正常执行
     */
    @Test
    public void test() {
        CommandHelloWorld cmd = new CommandHelloWorld(false);
        Assert.assertEquals("返回HelloWorld", "HelloWorld", cmd.execute());
    }

    /**
     * 测试execute异常执行，触发fallback
     */
    @Test
    public void testFallback() {
        HystrixCommand cmd = new CommandHelloWorld(true);

        Assert.assertEquals("返回fallback", "fallback", cmd.execute());
        Assert.assertTrue(cmd.isFailedExecution());
        Assert.assertTrue(cmd.isResponseFromFallback());
    }

    /**
     * 测试execute收到bad request，不会触发fallback
     */
    @Test(expected = com.netflix.hystrix.exception.HystrixBadRequestException.class)
    public void testBadRequset() {
        HystrixCommand cmd = new CommandHelloWorld(true, true);
        try {
            cmd.execute();
        } catch (HystrixBadRequestException ex) {
            //获得原始异常
            ex.getCause().printStackTrace();
            throw ex;
        }
    }

    /**
     * 测试触发了 HystrixRuntimeException,不会fallback
     */
    @Test(expected = com.netflix.hystrix.exception.HystrixRuntimeException.class)
    public void testHystrixRuntimeException() {
        HystrixCommand cmd = new CommandHelloWorld(true, false, false, true);
        try {
            cmd.execute();
        } catch (HystrixRuntimeException ex) {
            //获得原始异常
            ex.getCause().printStackTrace();
            throw ex;
        }
    }

    /**
     * 测试execute异常执行，触发网络调用实现的fallback
     */
    @Test
    public void testFallbackViaNetwork() {
        HystrixCommand cmd = new CommandHelloWorld(true, false, true);

        Assert.assertEquals("返回cache", "cache", cmd.execute());
        Assert.assertTrue(cmd.isFailedExecution());
        Assert.assertTrue(cmd.isResponseFromFallback());
    }

    /**
     * 测试一个command对象不能重复执行
     */
    @Test(expected = com.netflix.hystrix.exception.HystrixRuntimeException.class)
    public void testRepeat() {
        CommandHelloWorld cmd = new CommandHelloWorld(false);
        cmd.execute();
        try {
            cmd.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
