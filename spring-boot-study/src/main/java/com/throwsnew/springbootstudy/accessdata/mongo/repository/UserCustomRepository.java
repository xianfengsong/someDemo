package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import java.util.List;

public interface UserCustomRepository {

    User findUser(String userId, String userType,
            Long maxCreateTime, Integer size);

    void updateOrdersByPush(String userId, String userType, List<Order> orderList);
}
