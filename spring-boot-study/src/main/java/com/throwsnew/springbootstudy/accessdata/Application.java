package com.throwsnew.springbootstudy.accessdata;

import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import com.throwsnew.springbootstudy.accessdata.mongo.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;

@SpringBootApplication
@MapperScan("com.throwsnew.springbootstudy.accessdata.mysql.mapper")
public class Application implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 保存 合并订单记录
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

        System.out.println();


    }


}
