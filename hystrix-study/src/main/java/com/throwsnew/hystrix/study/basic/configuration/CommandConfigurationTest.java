package com.throwsnew.hystrix.study.basic.configuration;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 18-10-24 下午3:30 <br/>
 * Desc: 测试与熔断开关相关的配置
 * 官方更丰富的测试：
 * https://github.com/Netflix/Hystrix/blob/7f5a0afc23aa5ff82320560a04d4c81a45efd67c/hystrix-core/src/test/java/com/netflix/hystrix/HystrixCircuitBreakerTest.java
 */
public class CommandConfigurationTest {

    private int timeout = 100;
    private int statisticalWindowTime = 2_000;
    private int errorPercentage = 50;
    private int requestVolume = 10;
    private int sleepWindowMs = 5_000;
    private int poolSize = 20;
    private CommandConfigByCode.Setter setter;

    @Before
    public void init() {
        Hystrix.reset();
        setter = CommandConfigByCode.setter(
                ExecutionIsolationStrategy.THREAD,
                requestVolume,
                statisticalWindowTime,
                errorPercentage,
                poolSize,
                timeout,
                sleepWindowMs);
    }

    /**
     * 测试命令执行超时触发熔断（同样受窗口时间 请求阈值的限制）
     */
    @Test
    public void testCircuitOpen() throws InterruptedException {
        int runTime = timeout + 10;
        CommandConfigByCode command = new CommandConfigByCode(setter, runTime, true);
        command.execute();
        Assert.assertTrue("命令应该超时", command.isResponseTimedOut());
        //总执行次数满足阈值
        for (int i = 0; i < requestVolume; i++) {
            command = new CommandConfigByCode(setter, runTime, true);
            command.execute();
        }
        //等待窗口时间
        Thread.sleep(statisticalWindowTime);
        Assert.assertTrue("熔断打开", command.isCircuitBreakerOpen());
    }

    /**
     * 测试失败次数占比小于阈值，不会触发熔断
     */
    @Test
    public void testCircuitNotOpenByRate() {
        // 让请求次数超过阈值
        int requestNumber = requestVolume + 1;
        // 调整run time让所有测试的command 在同一窗口内执行完成
        int runTime = Math.max(statisticalWindowTime / requestNumber - 10, 0);
        // 根据失败率计算触发熔断的最小失败次数
        int minErrorTimes = (int) Math.ceil((double) requestNumber * errorPercentage / 100);
        HystrixCommand<String> cmd = null;
        for (int execTimes = 0; execTimes < requestNumber; execTimes++) {
            // 让命令失败次数小于 minErrorTimes-1
            if (execTimes < Math.max(minErrorTimes - 1, 0)) {
                cmd = new CommandConfigByCode(setter, runTime, false);
            } else {
                //让命令成功
                cmd = new CommandConfigByCode(setter, runTime, true);
            }
            cmd.execute();

        }
        Assert.assertFalse("熔断不会触发", cmd.isCircuitBreakerOpen());

    }

    /**
     * 熔断器打开后 在经过 sleepWindowInMilliseconds，变成半开状态，重新判断是否还要打开
     */
    @Test
    public void testSleepWindow() throws InterruptedException {
        //触发熔断开关
        HystrixCommand<String> cmd = null;
        for (int execTimes = 1; execTimes < requestVolume + 5; execTimes++) {
            cmd = new CommandConfigByCode(setter, 0, false);
            cmd.execute();
        }
        Thread.sleep(statisticalWindowTime);
        Assert.assertTrue("熔断打开", cmd.isCircuitBreakerOpen());

        //等待开关恢复到半开状态
        Thread.sleep(sleepWindowMs + 10);
        cmd = new CommandConfigByCode(setter, 0, true);
        Assert.assertTrue("熔断打开", cmd.isCircuitBreakerOpen());
        cmd.execute();
        Assert.assertFalse("熔断关闭", cmd.isCircuitBreakerOpen());
    }
}