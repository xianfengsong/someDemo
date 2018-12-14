package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mongodb.client.result.UpdateResult;
import com.throwsnew.springbootstudy.accessdata.mongo.model.Order;
import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Update.PushOperatorBuilder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public User findUser(String userId, String userType,
            Long maxCreateTime, Integer size) {

        MatchOperation matchUser = match(
                Criteria.where("userId").is(userId)
                        .andOperator(Criteria.where("userType").is(userType)));

        GroupOperation groupOperation = group("_id")
                .last("userType").as("userType")
                .last("userId").as("userId")
                .last("name").as("name")
                .push("orderList").as("orderList");
        MatchOperation matchCreateTime;
        if (maxCreateTime == 0L) {
            matchCreateTime = match(Criteria.where("orderList.createTime").gt(0L));
        } else {
            matchCreateTime = match(Criteria.where("orderList.createTime").lte(maxCreateTime));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                matchUser,
                unwind("orderList"),
                matchCreateTime,
                limit(size),
                groupOperation
        );
        AggregationResults<User> users = mongoTemplate
                .aggregate(aggregation, User.class, User.class);
        if (CollectionUtils.isEmpty(users.getMappedResults())) {
            return null;
        } else {
            return users.getMappedResults().get(0);
        }
    }
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
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("userType").is(userType));
        Update updateOld = new Update();
        updateOld.pull("orderList", Query.query(Criteria.where("_id").in(
                ids)));
        UpdateResult pullResult = mongoTemplate.updateFirst(query, updateOld, User.class);

        /*db.user.update(
            {"userId":"user9"},
            {$push:{orderList:{
                $each:[{"id":"4"},{"id":"42"}]
            }}}
        )*/
        Update updateNew = new Update();
        PushOperatorBuilder push = updateNew.push("orderList");
        push.each(orderList);
        push.sort(new Sort(Direction.DESC, "createTime"));
        UpdateResult pushResult = mongoTemplate
                .updateFirst(query, updateNew, User.class);

        Assert.isTrue(pullResult.wasAcknowledged() && pushResult.wasAcknowledged());
    }
}
