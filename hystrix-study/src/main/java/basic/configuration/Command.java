package basic.configuration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.util.Random;

/**
 * author Xianfeng <br/>
 * date 18-10-24 下午3:11 <br/>
 * Desc:
 */
public class Command extends HystrixCommand<String> {

    public Command(String name) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("g"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("cc"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("th"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(true)
                        .withCircuitBreakerForceOpen(false)
                        .withCircuitBreakerForceClosed(false)
                        .withCircuitBreakerErrorThresholdPercentage(5)
                        .withCircuitBreakerRequestVolumeThreshold(10)
                        .withCircuitBreakerSleepWindowInMilliseconds(5000)
                        .withExecutionTimeoutInMilliseconds(1000)
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter().withCoreSize(2).withMaxQueueSize(10)));
    }

    @Override
    protected String run() throws Exception {
        Random random = new Random();
        if (random.nextInt(2) == 1) {
            throw new Exception("xxx");
        }
        return "running";
    }

    @Override
    protected String getFallback() {
        return "fall";
    }
}
