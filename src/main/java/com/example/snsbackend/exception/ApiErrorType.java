package com.example.snsbackend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.", null, null),
    CONFLICT(HttpStatus.CONFLICT, "중복된 정보가 있습니다.", null, null),

    // JWT
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", null, null),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.", null, null),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "권한이 없는 토큰입니다.", null, null),

    // 회원 가입
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다.", null, null),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "잘못된 아이디 형식입니다.", null, null),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일입니다.", null, null),

    // 로그인
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.", null, null);

    private final HttpStatus status;
    private final String message;
    private final String details;
    private final String detailMessage;
}
