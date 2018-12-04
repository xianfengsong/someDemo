package com.throwsnew.springbootstudy.accessdata.mongo;

import java.util.List;

public interface UserCustomRepository {
     void updateOrders(String userId, List<Order> orderList);
}
