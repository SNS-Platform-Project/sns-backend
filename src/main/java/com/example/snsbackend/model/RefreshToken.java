package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;
    private String username;
    private String refreshToken;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}
