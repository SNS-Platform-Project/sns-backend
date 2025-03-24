package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Follow {
    @Field(name = "follow_id")
    private String followId;

    private String username;

    @Field(name = "created_at")
    private LocalDateTime createdAt;
}
