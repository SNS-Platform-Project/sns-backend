package com.example.snsbackend.repository;

import com.example.snsbackend.model.post.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
