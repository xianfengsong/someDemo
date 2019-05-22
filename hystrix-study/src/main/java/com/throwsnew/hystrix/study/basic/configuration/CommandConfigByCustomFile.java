package com.throwsnew.hystrix.study.basic.configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-5-22 下午4:21 <br/>
 * Desc:
 * hystrix默认的配置文件是config.properties
 * 可以通过修改jvm参数指定自定义文件位置
 * -Darchaius.configurationSource.additionalUrls=file:///root/workspace/someDemo/hystrix-study/src/main/resources/hystrix-config.properties
 * 如果还是放在/resources下,指定一个自定义的配置文件名就好
 * -Darchaius.configurationSource.defaultFileName=hystrix-config.properties
 */
public class CommandConfigByCustomFile {

    private HystrixCommand innerCommand;

    private void run() {
        innerCommand = new HystrixCommand<String>(HystrixCommandGroupKey.Factory.asKey("CommandConfigByCustomFile")) {
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

    @Test
    public void testUseCustomFile() throws InterruptedException {
        CommandConfigByCustomFile cmd = new CommandConfigByCustomFile();
        //文件配置的是12
        for (int i = 0; i < 12; i++) {
            cmd.run();
        }
        int myConfig = cmd.innerCommand.getProperties().circuitBreakerRequestVolumeThreshold().get();

        Assert.assertEquals("RequestVolume应该是12", 12, myConfig);
        Thread.sleep(1001L);
        Assert.assertTrue("会触发熔断", cmd.innerCommand.isCircuitBreakerOpen());
    }
}
