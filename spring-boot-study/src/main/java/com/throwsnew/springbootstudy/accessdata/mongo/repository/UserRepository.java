package com.throwsnew.springbootstudy.accessdata.mongo.repository;

import com.throwsnew.springbootstudy.accessdata.mongo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String>,UserCustomRepository {
}
