package com.example.snsbackend.jwt;

import com.example.snsbackend.model.RefreshToken;
import com.example.snsbackend.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtProvider {
    private SecretKey key;
    private long accessExpTime = 1800000;
    private long refreshExpTime = 3600000;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtProvider(@Value("${jwt.secret}") String key, RefreshTokenRepository refreshTokenRepository) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private LocalDateTime fromDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // id(username 또는 email)를 받아 새 토큰을 생성
    public JwtInfo generateToken(String id) {
        // 현재 시간
        Date now = new Date();
        long nowMiles = now.getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim("sub", id)
                .claim("ise", now)
                .claim("exp", new Date(nowMiles + accessExpTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim("sub", id)
                .claim("ise", now)
                .claim("exp", new Date(nowMiles + refreshExpTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshIseAt(fromDate(now))
                .refreshExpAt(fromDate(new Date(nowMiles + refreshExpTime)))
                .build();
    }

    // Access Token에서 인증 정보 추출
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseToken(accessToken);
        UserDetails principal = new User(claims.getSubject(), "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList());
    }

    // Refresh Token에서 인증 정보 추출
    public String getIdFromRefreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        return claims.getSubject();
    }

    //TODO: 로그아웃(블랙리스트) 생성 후 마저 작업할 것
    // 토큰 정보 검증
    /*public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token);
        return
    }*/

    // 토큰에서 claim 추출
    private Claims parseToken (String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Refresh Token 검증 (블랙리스트 구현 전이라 Access Token도 이걸로 검증)
    public boolean validateRefreshToken(String token) {
        try {
            String id = parseToken(token).getSubject();
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(id);
            return refreshToken.isPresent();
        } catch (ExpiredJwtException e) {
            log.info("Expired Refresh Token");
        } catch (SecurityException e) {
            log.info("Invalid Refresh Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Refresh Token");
        } catch (IllegalArgumentException e) {
            log.info("Refresh Token claims string is empty");
        }

        return false;
    }
}
