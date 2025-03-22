package com.example.snsbackend.repository;

import com.example.snsbackend.model.Follower;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FollowerRepository extends MongoRepository<Follower,String> {
    Follower findByUserId(String userId);
}
