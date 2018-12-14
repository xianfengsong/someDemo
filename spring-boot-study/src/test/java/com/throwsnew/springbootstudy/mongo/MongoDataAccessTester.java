package com.throwsnew.springbootstudy.mongo;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MongoDataAccessTester {

    @Autowired
    UserRepository repository;

    private final static Integer ORDER_SIZE = 10000;
    private final static Integer USER_NUMBER = 5000;

    @Test
    public void init() throws InterruptedException {
        List<Order> orders = getOrders(ORDER_SIZE);
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= USER_NUMBER; i++) {
            String type = "A";
            if (i % 2 == 0) {
                type = "B";
            }
            User user = getUser(type, orders);
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
    public void clear() {
        repository.deleteAll();
    }

    private User getUser(String type, List<Order> orders) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUserType(type);
        user.setName("user" + System.currentTimeMillis());
        user.setOrderList(orders);
        return user;
    }

    public static List<Order> getOrders(int size) {
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
}
