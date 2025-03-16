package com.example.snsbackend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "auth_code")
public class AuthCode {
    @Id
    private String id;

    private String email;
    private String authCode;

    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}
