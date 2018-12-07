package com.throwsnew.springbootstudy.mongo;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private final static Integer ORDER_SIZE = 9000;
    private final static Integer USER_NUMBER = 1000;

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
                Thread.sleep(1000L);
                System.out.println(i);
            }
        }
    }

    @Test
    public void updateByPush() {
        Long start = System.currentTimeMillis();
        String userId = "1654fa48-98e1-44c3-92d3-d0122dfe7272";
        String userType = "B";
        List<Order> orders = getOrders(100);
        orders.forEach(order -> order.setInfo("updated"));
        repository.updateOrdersByPush(userId, userType, getOrders(100));
        System.out.println("TIME:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void updateByReplace() {
        Long start = System.currentTimeMillis();
        String userId = "1654fa48-98e1-44c3-92d3-d0122dfe7272";
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
            order.setInfo("updated");
            mapById.put(order.getId(), order);
        }
        List<Order> mergeOrders = new ArrayList<>(mapById.values());
        mergeOrders.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        example.setOrderList(mergeOrders);
        repository.save(example);
        System.out.println("TIME:" + (System.currentTimeMillis() - start));
    }

    private User getUser(String type, List<Order> orders) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUserType(type);
        user.setName("user" + System.currentTimeMillis());
        user.setOrderList(orders);
        return user;
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
}
