package com.example.snsbackend.repository;

import com.example.snsbackend.model.BaseComment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseCommentRepository extends MongoRepository<BaseComment, String> {
}
