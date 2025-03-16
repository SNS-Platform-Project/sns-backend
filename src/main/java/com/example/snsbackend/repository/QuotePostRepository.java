package com.example.snsbackend.repository;

import com.example.snsbackend.model.QuotePost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotePostRepository extends MongoRepository<QuotePost, String> {
}
