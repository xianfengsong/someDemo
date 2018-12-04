package com.throwsnew.springbootstudy.accessdata.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

public class UserCustomRepositoryImpl implements UserCustomRepository {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public void updateOrders(String userId, List<Order> orderList) {
        List<String> newOrderIdList=orderList.stream().map(e->e.getId()).collect(Collectors.toList());
        Query query=new Query();
//        query.addCriteria(Criteria.where("orderList").elemMatch(Criteria.where("id").in(newOrderIdList)));

        Criteria[] c1 = new Criteria[newOrderIdList.size()];
        for(int i=0;i<newOrderIdList.size();i++){
            c1[i] = Criteria.where("orderList.id").is(orderList.get(i).getId());
        }
        Criteria criteria=new Criteria();
        criteria.andOperator(c1);
        query.addCriteria(criteria);

//        query.addCriteria(Criteria.where("_id").in(newOrderIdList));

        List<User> users=mongoTemplate.find(query,User.class);
        for(User u:users){
            System.out.println(u.getId());
        }
    }
}
