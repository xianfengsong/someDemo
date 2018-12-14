package com.throwsnew.springbootstudy.mongo;

import static com.throwsnew.springbootstudy.mongo.MongoDataAccessTester.getOrders;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Example;

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

    /**
     * 用findOne查询
     * todo 禁用查询缓存
     */
//    @Benchmark
    public void findOne() {
        User user = new User();
        user.setUserType("B");
        Optional<User> result = repository.findOne(Example.of(user));
        Assert.assertTrue(result.isPresent());
    }

    /**
     * 用聚合查询 返回100条order
     * todo 禁用查询缓存,选择不同的userId
     */
//    @Benchmark
    public void findByAggregation() {
        String userId = "819687c5-eb76-4658-bc91-7ad1e41107e3";
        String userType = "B";
        User user = repository.findUser(userId, userType, System.currentTimeMillis(), 100);
        if (user != null) {
            Assert.assertEquals(100, user.getOrderList().size());
            System.out.println(user.getName());
        }
    }

    @Benchmark
    public void updateByPush() {
        String userId = "2d8c6abf-7a59-40df-acd5-7c0144de82fb";
        String userType = "B";
        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updateByPush"));
        repository.updateOrdersByPush(userId, userType, orders);
    }

    //    @Benchmark
    public void updateByReplace() {
        String userId = "91886e2a-8cb9-487a-b083-e29ddb5cddc0";
        User example = new User();
        example.setUserType("B");
        example.setUserId(userId);
        List<Order> oldOrders = repository.findOne(Example.of(example)).map(User::getOrderList)
                .orElse(Collections.emptyList());
        List<Order> newOrders = getOrders(100);
        Map<String, Order> mapById = new HashMap<>();
        for (Order order : oldOrders) {
            mapById.put(order.getId(), order);
        }
        for (Order order : newOrders) {
            order.setInfo("updateByReplace");
            mapById.put(order.getId(), order);
        }
        List<Order> mergeOrders = new ArrayList<>(mapById.values());
        mergeOrders.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        example.setOrderList(mergeOrders);
        repository.save(example);
    }


    @Test
    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(1)
                .build();
        new Runner(opt).run();
    }
}
