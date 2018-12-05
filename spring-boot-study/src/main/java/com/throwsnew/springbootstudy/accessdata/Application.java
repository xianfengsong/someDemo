package com.throwsnew.springbootstudy.accessdata;

import com.throwsnew.springbootstudy.accessdata.mongo.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.User;
import com.throwsnew.springbootstudy.accessdata.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;

import java.util.*;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 保存 合并订单记录
     *
     * @param user
     */
    public void saveUser(User user) {
        List<Order> oldOrders = repository.findOne(Example.of(user))
                .map(User::getOrderList)
                .orElse(Collections.emptyList());
        List<Order> newOrders = user.getOrderList();
        Map<String, Order> combineById = new LinkedHashMap<>();
        for (Order newOrder : newOrders) {
            combineById.put(newOrder.getId(), newOrder);
        }
        for (Order oldOrder : oldOrders) {
            combineById.putIfAbsent(oldOrder.getId(), oldOrder);
        }
        user.setOrderList(new ArrayList<>(combineById.values()));
        repository.save(user);
    }
    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

        // save a couple of customers
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId("id" + String.valueOf(i));
            user.setName("name" + i);
            user.setOrderList(getOrderList(i));
            repository.save(user);
        }

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }

        repository.updateOrders("", getOrderList(5));

        System.out.println();


    }

    private List<Order> getOrderList(int size) {
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Order order = new Order();
            order.setCreateTime(new Date());
            order.setId("order" + i);
            order.setInfo("some products");
            orderList.add(order);
        }
        return orderList;
    }


}
