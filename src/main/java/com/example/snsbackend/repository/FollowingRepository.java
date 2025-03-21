package com.example.snsbackend.repository;

import com.example.snsbackend.model.Following;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FollowingRepository extends MongoRepository<Following,String> {

    Following findByUserId(String userId);
}
