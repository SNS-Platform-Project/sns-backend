package com.example.snsbackend.model;

import com.example.snsbackend.dto.ChatRoomRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "chat_rooms")
public class ChatRoom {
    @AllArgsConstructor
    @Getter
    @Setter
    static class LastMessage {
        @Field("message_id")
        private String messageId;
        @Field("sender_id")
        private String senderId;
        private String content;
        @Field("sent_at")
        private Date sentAt;
    }

    @Id
    private String id;

    private String name;
    private String type;    // private or group
    private List<String> participants;
    @Field("created_at")
    private Date createdAt;
    @Field("last_message")
    private LastMessage lastMessage;

    public ChatRoom(ChatRoomRequest request) {
        this.name = request.getName();
        this.type = request.getUserIds().size() > 2 ? "group" : "private";
        this.participants = request.getUserIds();
        this.createdAt = new Date();
        this.lastMessage = null;
    }

    @JsonIgnore
    public String getLastMessageId() {
        return lastMessage.getMessageId();
    }
}
