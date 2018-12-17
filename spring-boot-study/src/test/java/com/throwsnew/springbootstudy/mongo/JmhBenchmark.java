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

    @Test
    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(10)
                .build();
        new Runner(opt).run();
    }

//    @Benchmark
public void findBySlice() {
        User user = new User();
    user.setUserId("dac6899b-6417-4e4b-8354-16acdc55a661");
    user.setUserType("A");
    User result = repository.findUserBySlice(user.getUserId(), user.getUserType(), 100);
    Assert.assertNotNull(result);
    }

    /**
     * 用聚合查询 返回100条order
     */
//    @Benchmark
    public void findByAggregation() {
        String userId = "063cb079-0118-4c8f-b121-4c702a229377";
        String userType = "B";
        User user = repository.findUserByAggr(userId, userType, System.currentTimeMillis(), 100);
        if (user != null) {
            Assert.assertEquals(100, user.getOrderList().size());
        }
    }

    @Benchmark
    public void updateByPush() {
        String userId = "81dc5e77-48b0-48a4-b76c-bc4091484573";
        String userType = "A";
        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updateByPush" + System.currentTimeMillis()));
        repository.updateOrdersByPush(userId, userType, orders);
    }

    //    @Benchmark
    public void updateByReplace() {
        String userId = "06dc220e-fc5e-42e7-9060-9c5ded984ced";
        User example = new User();
        example.setUserType("A");
        example.setUserId(userId);
        List<Order> oldOrders = repository.findOne(Example.of(example)).map(User::getOrderList)
                .orElse(Collections.emptyList());
        List<Order> newOrders = getOrders(100);
        Map<String, Order> mapById = new HashMap<>();
        for (Order order : oldOrders) {
            mapById.put(order.getId(), order);
        }
        for (Order order : newOrders) {
            order.setInfo("updateByReplace" + System.currentTimeMillis());
            mapById.put(order.getId(), order);
        }
        List<Order> mergeOrders = new ArrayList<>(mapById.values());
        mergeOrders.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        example.setOrderList(mergeOrders);
        repository.save(example);
    }


}
