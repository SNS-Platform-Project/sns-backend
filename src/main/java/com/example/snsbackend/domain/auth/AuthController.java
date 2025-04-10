package com.example.snsbackend.domain.auth;

import com.example.snsbackend.dto.*;
import com.example.snsbackend.jwt.JwtInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ApiResponse.success();
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    // 이메일 인증 요청
    @PostMapping("/email/verify-request")
    public ResponseEntity<?> sendAuthCodeEmail(@RequestBody @Valid EmailRequest request) {
        authService.sendCodeToEmail(request);
        return ApiResponse.success();
    }

    // 이메일 인증번호 확인
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid AuthCodeRequest request) {
        authService.verifyEmail(request);
        return ApiResponse.success();
    }

    // 비밀번호 초기화
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid LoginRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success();
    }
}
