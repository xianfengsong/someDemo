package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import java.util.List;

public interface UserCustomRepository {

     void updateOrdersByPush(String userId, String userType, List<Order> orderList);
}
