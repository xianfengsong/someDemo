package basic;

import basic.configuration.CommandWithConfiguration;
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
    private int errorPercentage = 5;
    private int requestVolume = 10;
    private int sleepWindowMs = 5_000;
    private int poolSize = 20;
    private CommandWithConfiguration.Setter setter;

    @Before
    public void init() {
        Hystrix.reset();
        setter = CommandWithConfiguration.setter(
                ExecutionIsolationStrategy.THREAD,
                requestVolume,
                statisticalWindowTime,
                errorPercentage,
                poolSize,
                timeout,
                sleepWindowMs);
    }

    /**
     * 命令执行超时触发熔断（同样受窗口时间 请求阈值的限制）
     */
    @Test
    public void testTimeout() throws InterruptedException {
        int runTime = timeout + 10;
        CommandWithConfiguration command = new CommandWithConfiguration(setter, runTime, true);
        String result = command.execute();
        //命令运行超时 fallback
        Assert.assertEquals("fallback", result);
        //执行次数满足阈值
        for (int i = 0; i < requestVolume; i++) {
            CommandWithConfiguration command2 = new CommandWithConfiguration(setter, runTime, true);
            command2.execute();
        }
        //等待窗口时间
        Thread.sleep(statisticalWindowTime);
        Assert.assertTrue("熔断打开", command.isCircuitBreakerOpen());
        System.out.println(String.format("timeout %d runTime %d open? %s", timeout, runTime,
                command.isCircuitBreakerOpen()));

    }

    /**
     * 在窗口时间内 执行的请求数超过阈值 且 失败占比超过限制 会触发熔断
     */
    @Test
    public void testRequestVolume() {
        // 请求次数超过阈值
        int requestNumber = requestVolume * 2;
        // 调整run time让所有测试的command 在同一窗口内执行完成
        int runTime = statisticalWindowTime / requestNumber - 10;

        for (int execTimes = 1; execTimes < requestNumber; execTimes++) {
            // 让命令全部失败
            HystrixCommand<String> cmd = new CommandWithConfiguration(setter, runTime, false);
            cmd.execute();
            if (execTimes <= requestVolume) {
                Assert.assertFalse("熔断关闭", cmd.isCircuitBreakerOpen());
            } else {
                Assert.assertTrue("熔断打开", cmd.isCircuitBreakerOpen());
            }
        }
    }

    /**
     * 熔断器打开后 在经过 sleepWindowInMilliseconds，变成半开状态，重新判断是否还要打开
     */
    @Test
    public void testSleepWindow() throws InterruptedException {
        //触发熔断开关
        HystrixCommand<String> cmd = new CommandWithConfiguration(setter, 0, false);
        ;
        for (int execTimes = 1; execTimes < requestVolume + 5; execTimes++) {
            cmd = new CommandWithConfiguration(setter, 0, false);
            cmd.execute();
        }
        Thread.sleep(statisticalWindowTime);
        Assert.assertTrue("熔断打开", cmd.isCircuitBreakerOpen());

        //等待开关回复到半开状态
        Thread.sleep(sleepWindowMs + 10);
        HystrixCommand<String> newCmd = new CommandWithConfiguration(setter, 0, true);
        newCmd.execute();
        Assert.assertFalse("熔断关闭", cmd.isCircuitBreakerOpen());
    }
}
