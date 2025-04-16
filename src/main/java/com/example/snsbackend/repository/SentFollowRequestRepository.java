package com.example.snsbackend.repository;

import com.example.snsbackend.model.SentFollowRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SentFollowRequestRepository extends MongoRepository<SentFollowRequest, String> {
    SentFollowRequest findByUserId(String userId);
}
