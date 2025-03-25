package com.example.snsbackend.jwt;

import com.example.snsbackend.model.AccessTokenBlackList;
import com.example.snsbackend.model.RefreshToken;
import com.example.snsbackend.repository.AccessTokenBlackListRepository;
import com.example.snsbackend.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtProvider {
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;
    private SecretKey key;

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtProvider(@Value("${jwt.secret}") String key, RefreshTokenRepository refreshTokenRepository, CustomUserDetailsService customUserDetailsService, AccessTokenBlackListRepository accessTokenBlackListRepository) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.refreshTokenRepository = refreshTokenRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.accessTokenBlackListRepository = accessTokenBlackListRepository;
    }

    private LocalDateTime fromDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // id(username 또는 email)를 받아 새 토큰을 생성
    public JwtInfo generateToken(String userId) {
        // 현재 시간
        Date now = new Date();
        long nowMiles = now.getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim("sub", userId)
                .claim("ise", now)
                .claim("exp", new Date(nowMiles + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim("sub", userId)
                .claim("ise", now)
                .claim("exp", new Date(nowMiles + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshIseAt(fromDate(now))
                .refreshExpAt(fromDate(new Date(nowMiles + REFRESH_TOKEN_EXPIRE_TIME)))
                .build();
    }

    // Access Token에서 인증 정보 추출
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseToken(accessToken);
        // claims에 저장된 Sub(user id)로 기타 정보 검색
        UserDetails userDetails = customUserDetailsService.loadUserById(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    // Refresh Token에서 인증 정보 추출
    public String getIdFromRefreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        return claims.getSubject();
    }

    // 토큰 정보 검증
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        Optional<AccessTokenBlackList> accessToken = accessTokenBlackListRepository.findByAccessToken(token);
        return accessToken.isEmpty();
    }

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
