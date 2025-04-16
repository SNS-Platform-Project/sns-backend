package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "received_follow_requests")
public class ReceivedFollowRequest {
    @Id
    private String id;

    @Field(name = "user_id")
    @Indexed(unique = true)
    private String userId;

    private List<Follow> followers;
}
