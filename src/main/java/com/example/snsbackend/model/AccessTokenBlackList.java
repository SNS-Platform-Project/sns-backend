package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "access_token_blacklist")
public class AccessTokenBlackList {
    @Id
    private String id;

    @Field(name = "access_token")
    private String accessToken;

    @Field(name = "black_at")
    private LocalDateTime blackAt;
}
