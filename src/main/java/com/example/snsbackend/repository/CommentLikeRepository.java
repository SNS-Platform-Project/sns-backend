package com.example.snsbackend.repository;

import com.example.snsbackend.model.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Map;

public interface CommentLikeRepository extends MongoRepository<CommentLike, String> {
}
