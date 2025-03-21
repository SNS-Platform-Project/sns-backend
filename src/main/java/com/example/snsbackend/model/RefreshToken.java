package com.example.snsbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;

    @NotBlank
    private String userId;

    @Field(name = "refresh_token")
    private String refreshToken;

    @Field(name = "issued_at")
    private LocalDateTime issuedAt;

    @Field(name = "expired_at")
    private LocalDateTime expiredAt;
}
