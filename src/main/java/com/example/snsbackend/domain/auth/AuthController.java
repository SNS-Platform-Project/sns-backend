package com.example.snsbackend.domain.auth;

import com.example.snsbackend.dto.AuthCodeRequest;
import com.example.snsbackend.dto.EmailRequest;
import com.example.snsbackend.dto.LoginRequest;
import com.example.snsbackend.dto.RegisterRequest;
import com.example.snsbackend.jwt.JwtInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    // 회원 가입
    @PostMapping("/register")
    public JwtInfo register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    // 로그인
    @PostMapping("/login")
    public JwtInfo login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    // 이메일 인증 요청
    @PostMapping("/email/verify-request")
    public ResponseEntity<?> sendAuthCodeEmail(@RequestBody @Valid EmailRequest request) {
        return authService.sendCodeToEmail(request);
    }

    // 이메일 인증번호 확인
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid AuthCodeRequest request) {
        return authService.verifyEmail(request);
    }
}
