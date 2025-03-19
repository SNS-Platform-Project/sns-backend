package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "auth_code")
public class AuthCode {
    @Id
    private String id;

    private String email;

    @Field(name = "code")
    private String authCode;

    @Field(name = "email_verified")
    private boolean emailVerified;

    @Field(name = "issued_at")
    private LocalDateTime issuedAt;

    @Field(name = "expired_at")
    private LocalDateTime expiredAt;
}
