package com.throwsnew.springbootstudy.accessdata.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String>,UserCustomRepository {
}
