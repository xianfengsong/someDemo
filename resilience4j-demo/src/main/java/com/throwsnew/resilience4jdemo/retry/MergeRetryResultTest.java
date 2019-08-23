package com.throwsnew.resilience4jdemo.retry;

import static io.github.resilience4j.retry.RetryConfig.DEFAULT_WAIT_DURATION;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-23 下午8:31 <br/>
 * Desc:
 */
public class MergeRetryResultTest {

    //创建一个重试器配置，返回结果小于2，触发重试
    private final RetryConfig commonConfig = RetryConfig.<List<String>>custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(DEFAULT_WAIT_DURATION))
            .retryOnResult(strings -> strings.size() < 10)
            .build();

    @Test
    public void mergeRetryResult() {
        Retry retry = Retry.of("sync retry", commonConfig);
        List<String> list = new ArrayList<>();
        FillService fillService = source -> {
            source.add("hi");
            return source;
        };
        try {
            retry.executeCallable(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    return fillService.fill(list);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(list);
    }

    interface FillService {

        List<String> fill(List<String> source);
    }
}
