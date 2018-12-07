package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import com.mongodb.client.result.UpdateResult;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Update.PushOperatorBuilder;
import org.springframework.util.Assert;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 更新用户订单 通过pull push更新order数组
     */
    @Override
    public void updateOrdersByPush(String userId, String userType, List<Order> orderList) {
        /*
        db.user.update({"userId":"user9"},
            {$pull:
                {orderList:{
                    _id:{$in:["1","2","3"]}
                }}
            })
        */
        List<String> ids = orderList.stream().map(Order::getId).collect(Collectors.toList());
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .andOperator(Criteria.where("userType").is("A")));
        Update updateOld = new Update();
        updateOld.pull("orderList", Query.query(Criteria.where("id").in(
                ids)));
        UpdateResult pullResult = mongoTemplate
                .updateFirst(query, updateOld, Order.class);

        /*db.user.update(
            {"userId":"user9"},
            {$push:{orderList:{
                $each:[{"id":"4"},{"id":"42"}]
            }}}
        )*/
        Update updateNew = new Update();
        PushOperatorBuilder push = updateNew.push("histories");
        push.each(orderList);
        push.sort(new Sort(Direction.DESC, "createTime"));
        UpdateResult pushResult = mongoTemplate
                .updateFirst(query, updateNew, Order.class);

        Assert.isTrue(pullResult.wasAcknowledged() && pushResult.wasAcknowledged());

    }
}
