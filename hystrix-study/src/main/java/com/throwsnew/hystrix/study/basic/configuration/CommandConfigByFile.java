package com.throwsnew.hystrix.study.basic.configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-5-22 下午3:34 <br/>
 * Desc:
 * 测试使用文件可以配置hystrix
 * hystrix.command.default.xxx是默认全局配置
 * hystrix.command.{HystrixCommandKey}.xxx 可以对command单独配置
 */
public class CommandConfigByFile {

    private HystrixCommand innerCommand;

    private void runInDefaultConfig() {
        innerCommand = new HystrixCommand<String>(HystrixCommandGroupKey.Factory.asKey("CommandConfigByFile")) {
            @Override
            protected String run() throws Exception {
                throw new Exception("always fail");
            }

            @Override
            protected String getFallback() {
                return "fallback";
            }
        };
        innerCommand.execute();
    }

    private void runInCustomConfig() {
        innerCommand = new CustomCommand(HystrixCommandGroupKey.Factory.asKey("CommandConfigByFile"));
        innerCommand.execute();
    }

    /**
     * 测试初始化的innerCommand,会使用config.properties中的默认配置
     *
     * hystrix.command.default.circuitBreaker.requestVolumeThreshold=6
     * hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds=1000
     *
     * @throws InterruptedException e
     */
    @Test
    public void testUseDefaultInFile() throws InterruptedException {
        CommandConfigByFile cmd = new CommandConfigByFile();
        //文件配置的是6
        for (int i = 0; i < 6; i++) {
            cmd.runInDefaultConfig();
        }
        int myConfig = cmd.innerCommand.getProperties().circuitBreakerRequestVolumeThreshold().get();

        Assert.assertEquals("RequestVolume应该是6", 6, myConfig);
        Thread.sleep(1001L);
        Assert.assertTrue("会触发熔断", cmd.innerCommand.isCircuitBreakerOpen());
    }

    /**
     * 测试初始化的CustomCommand，会在配置文件中使用hystrix.command.CustomCommand的配置
     * {HystrixCommandKey} 默认会使用command实例的类名，就是CustomCommand
     * hystrix.command.CustomCommand.circuitBreaker.requestVolumeThreshold=3
     */
    @Test
    public void testUseCustomInFile() throws InterruptedException {
        CommandConfigByFile cmd = new CommandConfigByFile();
        //文件配置的是3
        for (int i = 0; i < 3; i++) {
            cmd.runInCustomConfig();
        }
        int myConfig = cmd.innerCommand.getProperties().circuitBreakerRequestVolumeThreshold().get();
        Assert.assertEquals("RequestVolume应该是3", 3, myConfig);
        Thread.sleep(1001L);
        Assert.assertTrue("会触发熔断", cmd.innerCommand.isCircuitBreakerOpen());
    }

    class CustomCommand extends HystrixCommand<String> {

        protected CustomCommand(HystrixCommandGroupKey group) {
            super(group);
        }

        @Override
        protected String run() throws Exception {
            throw new Exception("always fail");
        }

        @Override
        protected String getFallback() {
            return "fallback";
        }
    }
}
