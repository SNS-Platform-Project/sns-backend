package com.example.snsbackend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.", null, null),
    CONFLICT(HttpStatus.CONFLICT, "중복된 정보가 있습니다.", null, null),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었습니다.", null, null),

    // 회원 가입
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다.", null, null),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "잘못된 아이디 형식입니다.", null, null),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일입니다.", null, null),

    // 로그인
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.", null, null),

    // Token
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "해당 Refresh Token을 찾을 수 없습니다.", null, null),
    UNAUTHORIZED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Refresh Token입니다.",null, null),
    BAD_REQUEST_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 Refresh Token입니다.",null, null);

    private final HttpStatus status;
    private final String message;
    private final String details;
    private final String detailMessage;
}
