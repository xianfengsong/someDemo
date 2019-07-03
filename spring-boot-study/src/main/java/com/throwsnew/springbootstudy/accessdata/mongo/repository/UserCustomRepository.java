package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import java.util.List;

public interface UserCustomRepository {

    /**
     * 聚合查询用户信息
     *
     * @param userId userId
     * @param userType userType
     * @param maxCreateTime maxCreateTime
     * @param size 订单分页
     * @return user
     */
    User findUserByAggr(String userId, String userType,
            Long maxCreateTime, Integer size);

    /**
     * 查询用户信息 用slice分页订单(满足不了按照时间查询订单)
     *
     * @param userId id
     * @param userType type
     * @param size 订单分页
     * @return user
     */
    User findUserBySlice(String userId, String userType, Integer size);


    void updateOrdersByPush(String userId, String userType, List<Order> orderList);
}
