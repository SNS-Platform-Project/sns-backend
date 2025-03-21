package com.example.snsbackend.repository;

import com.example.snsbackend.model.RegularPost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegularPostRepository extends MongoRepository<RegularPost, String> {
}
