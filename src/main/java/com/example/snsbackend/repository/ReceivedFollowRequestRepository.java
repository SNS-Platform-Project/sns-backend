package com.example.snsbackend.repository;

import com.example.snsbackend.model.ReceivedFollowRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceivedFollowRequestRepository extends MongoRepository<ReceivedFollowRequest, String> {
    ReceivedFollowRequest findByUserId(String userId);
}
