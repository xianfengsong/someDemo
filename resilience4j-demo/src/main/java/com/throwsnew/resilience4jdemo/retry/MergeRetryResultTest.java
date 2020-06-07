package com.throwsnew.resilience4jdemo.retry;

import static io.github.resilience4j.retry.RetryConfig.DEFAULT_WAIT_DURATION;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-23 下午8:31 <br/>
 * Desc: 利用retry合并几次重试结果
 */
public class MergeRetryResultTest {

    /**
     * 创建一个重试器配置，最多执行10次，RuntimeException触发重试
     */
    private final RetryConfig commonConfig = RetryConfig.<List<String>>custom()
            .maxAttempts(10)
            .waitDuration(Duration.ofMillis(DEFAULT_WAIT_DURATION))
            .retryExceptions(RuntimeException.class)
            .build();

    /**
     * 测试：填充一个list,目标为10个元素
     * 每次添加一个元素，list个数小于目标就抛出异常，触发重试
     * 期望：最后list填充完成
     */
    @Test
    public void mergeRetryResult() {
        Retry retry = Retry.of("sync retry", commonConfig);

        FillService fillService = (source, p) -> {
            source.add("hi " + p.getName());
            if (source.size() < p.getCount()) {
                System.out.println(source.size() + ":" + p.getCount());
                throw new RuntimeException("数量不足");
            }
            return source;
        };

        List<String> list = new ArrayList<>();
        Param param = new Param("jack", 10);
        try {
            retry.executeCallable(() -> fillService.fill(list, param));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long retryFail = retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt();
        Long retrySuccess = retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt();
        Assert.assertEquals("执行retry失败次数为0", 0L, retryFail.longValue());
        Assert.assertEquals("执行retry成功次数为1", 1L, retrySuccess.longValue());
        Assert.assertEquals("经过重试list填充完成", 10, list.size());
        System.out.println(list);
    }

    interface FillService {

        List<String> fill(List<String> source, Param param);
    }

    class Param {

        String name;
        Integer count;

        Param(String name, Integer count) {
            this.name = name;
            this.count = count;
        }

        String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        Integer getCount() {
            return count;
        }

        void setCount(Integer count) {
            this.count = count;
        }
    }
}
