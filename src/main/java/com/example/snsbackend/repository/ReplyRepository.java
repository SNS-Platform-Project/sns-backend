package com.example.snsbackend.repository;

import com.example.snsbackend.model.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReplyRepository extends MongoRepository<Reply, String> {
}
