package com.throwsnew.springbootstudy.mongo;

import com.alibaba.fastjson.JSON;
import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MongoDataAccessTester {

    @Autowired
    UserRepository repository;

    private final static Integer ORDER_SIZE = 10000;
    private final static Integer USER_NUMBER = 5000;
    final static String USER_TYPE = "TYPE";
    private final static Random RANDOM = new Random();

    static List<Order> getOrders(int size) {
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

    static String randomUserId() {
        int id = RANDOM.nextInt(USER_NUMBER);
        if (id == 0) {
            id++;
        }
        return "u" + id;
    }

    @Test
    public void init() throws InterruptedException {
        List<Order> orders = getOrders(ORDER_SIZE);
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= USER_NUMBER; i++) {

            User user = getUser("u" + i, orders);
            users.add(user);
            if (i % 100 == 0) {
                repository.saveAll(users);
                users.clear();
                Thread.sleep(3000L);
                System.out.println(i);
            }
        }
    }

    @Test
    public void slice() {
        String userId = randomUserId();
        System.out.println(userId);
        User user = repository.findUserBySlice(userId, USER_TYPE, 100);

        Assert.assertNotNull(user);
    }

    @Test
    public void one() {
        User example = new User();
        example.setUserId("u1");
        User user = repository.findOne(Example.of(example)).orElse(new User());
        System.out.println(JSON.toJSONString(user, false).getBytes().length / 1024 + "kb");
    }
    @Test
    public void clear() {
        repository.deleteAll();
    }

    @Test
    public void aggregation() {
        String userId = randomUserId();
        System.out.println(userId);

        User user = repository.findUserByAggr(userId, USER_TYPE,
                System.currentTimeMillis(), 100);
        Assert.assertNotNull(user);
    }

    @Test
    public void updateByPush() {
        String userId = randomUserId();
        String userType = "C";
        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updateByPush"));
        repository.updateOrdersByPush(userId, userType, orders);
        User result = repository.findUserByAggr(userId, userType, Long.MAX_VALUE, 100);
        Assert.assertEquals(100, result.getOrderList().size());
    }

    private User getUser(String id, List<Order> orders) {
        User user = new User();
        user.setUserId(id);
        user.setUserType(USER_TYPE);
        user.setName("user" + System.currentTimeMillis());
        user.setOrderList(orders);
        return user;
    }

    @Test
    public void t() {
        int i = 0;
        while (i < USER_NUMBER) {
            System.out.println(RANDOM.nextInt(USER_NUMBER));
            i++;
        }
    }
}
