package com.example.snsbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;

    @NotBlank
    private String email;

    private String refreshToken;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}
