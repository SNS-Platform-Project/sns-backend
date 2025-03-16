package com.example.snsbackend.repository;

import com.example.snsbackend.model.Repost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepostRepository extends MongoRepository<Repost, String> {
}
