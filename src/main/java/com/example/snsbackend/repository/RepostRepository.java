package com.example.snsbackend.repository;

import com.example.snsbackend.model.Repost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepostRepository extends MongoRepository<Repost, String> {
    void deleteByUserIdAndPostId(String userId, String postId);

    List<Repost> findByUserId(String userId);
}
