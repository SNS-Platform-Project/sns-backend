package com.example.snsbackend.repository;

import com.example.snsbackend.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findById(String id);
}
