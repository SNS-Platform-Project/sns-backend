package com.example.snsbackend.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class JwtInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    private LocalDateTime refreshIseAt;
    private LocalDateTime refreshExpAt;
}
