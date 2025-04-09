package com.example.snsbackend.domain.chat;

import com.example.snsbackend.dto.SendMessage;
import com.example.snsbackend.model.ChatRoom;
import com.example.snsbackend.model.Message;
import com.example.snsbackend.repository.ChatMessageRepository;
import com.example.snsbackend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MongoTemplate mongoTemplate;

    public Message createMessage(String roomId, String senderId, SendMessage sendMessage) {
        if (!chatRoomRepository.existsById(roomId)) {
            log.error("Invalid room ID {}", roomId);
            throw new IllegalArgumentException("Invalid room ID");
        }
        Message message = new Message(roomId, senderId, sendMessage);
        chatMessageRepository.save(message);

        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(roomId)),
                new Update().set("lastMessage.senderId", senderId)
                        .set("lastMessage.content", sendMessage.getContent())
                        .set("lastMessage.messageId", message.getId())
                        .set("lastMessage.sentAt", new Date()), ChatRoom.class);
        return message;
    }
}
