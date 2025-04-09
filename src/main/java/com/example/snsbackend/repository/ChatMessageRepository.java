package com.example.snsbackend.repository;

import com.example.snsbackend.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<Message, String> {
}
