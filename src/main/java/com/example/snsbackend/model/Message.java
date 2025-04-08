package com.example.snsbackend.model;

import com.example.snsbackend.dto.SendMessage;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document("chat_messages")
public class Message {
    @Id
    private String id;

    @Field("room_id")
    private String roomId;
    @Field("sender_id")
    private String senderId;
    private String content;
    @Field("message_type")
    private String messageType;
    @Field("read_by")
    private List<String> readBy;
    @Field("created_at")
    private Date createdAt;

    public Message(String roomId, String senderId, SendMessage sendMessage) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = sendMessage.getContent();
        this.messageType = sendMessage.getType();
        this.readBy = new ArrayList<>();
        this.createdAt = new Date();
    }
}
