package com.throwsnew.springbootstudy.mongo;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author Xianfeng <br/>
 * date 18-12-7 下午7:42 <br/>
 * Desc:
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class JmhBenchmark {
    private UserRepository repository;
    private ConfigurableApplicationContext applicationContext;

    @Setup
    public void setUp() {
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(Application.class);
        }
        repository = applicationContext.getBean(UserRepository.class);
    }

    @TearDown
    public void tearDown() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
    @Benchmark
    public void updateByPush() {
        String userId = "1654fa48-98e1-44c3-92d3-d0122dfe7272";
        String userType = "B";
        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updated"));
        repository.updateOrdersByPush(userId, userType, getOrders(100));
    }

    private List<Order> getOrders(int size) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long time = System.currentTimeMillis();
            Order order = new Order();
            order.setCreateTime((time - time % size) + i);
            order.setId("order" + i);
            order.setInfo("Info:" + order.getId() + order.getCreateTime());
            orders.add(order);
        }
        return orders;
    }
    @Test
    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
