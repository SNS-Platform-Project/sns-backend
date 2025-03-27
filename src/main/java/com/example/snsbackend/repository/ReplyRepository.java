package com.example.snsbackend.repository;

import com.example.snsbackend.model.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReplyRepository extends MongoRepository<Reply, String> {
    List<Reply> findByParentId(String parentId);
}
