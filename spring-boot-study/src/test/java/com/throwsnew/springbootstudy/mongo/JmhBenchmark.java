package com.throwsnew.springbootstudy.mongo;

import static com.throwsnew.springbootstudy.mongo.MongoDataAccessTester.USER_TYPE;
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
    user.setUserId(MongoDataAccessTester.randomUserId());
    user.setUserType(USER_TYPE);

    User result = repository.findUserBySlice(user.getUserId(), user.getUserType(), 100);
    Assert.assertNotNull(result);
    }

//    @Benchmark
    public void findByAggregation() {
        String userId = MongoDataAccessTester.randomUserId();
        User user = repository.findUserByAggr(userId, USER_TYPE, System.currentTimeMillis(), 100);
        if (user != null) {
            Assert.assertEquals(100, user.getOrderList().size());
        }
    }

    @Benchmark
    public void updateByPush() {
        String userId = MongoDataAccessTester.randomUserId();

        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updateByPush" + System.currentTimeMillis()));
        repository.updateOrdersByPush(userId, USER_TYPE, orders);
    }

    //    @Benchmark
    public void updateByReplace() {
        String userId = MongoDataAccessTester.randomUserId();
        User example = new User();
        example.setUserType(USER_TYPE);
        example.setUserId(userId);
        Optional<User> oldUser = repository.findOne(Example.of(example));
        List<Order> oldOrders = oldUser.map(User::getOrderList)
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
        example.set_id(oldUser.map(User::get_id).orElse(null));
        repository.save(example);
    }


}
