package com.example.snsbackend.repository;

import com.example.snsbackend.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
