package com.example.snsbackend.repository;

import com.example.snsbackend.model.PostLike;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostLikeRepository extends MongoRepository<PostLike, String> {
    void deleteByPostIdAndUserId(String postId, String userId);
}
