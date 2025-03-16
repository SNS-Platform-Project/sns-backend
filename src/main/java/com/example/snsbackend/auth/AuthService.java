package com.example.snsbackend.auth;

import com.example.snsbackend.dto.EmailRequest;
import com.example.snsbackend.dto.LoginRequest;
import com.example.snsbackend.dto.RegisterRequest;
import com.example.snsbackend.email.EmailService;
import com.example.snsbackend.jwt.JwtInfo;
import com.example.snsbackend.jwt.JwtProvider;
import com.example.snsbackend.mapper.AuthCodeMapper;
import com.example.snsbackend.mapper.ProfileMapper;
import com.example.snsbackend.mapper.RefreshTokenMapper;
import com.example.snsbackend.model.AuthCode;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.model.RefreshToken;
import com.example.snsbackend.repository.AuthCodeRepository;
import com.example.snsbackend.repository.ProfileRepository;
import com.example.snsbackend.repository.RefreshTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthCodeMapper authCodeMapper;
    private final ProfileMapper profileMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final ProfileRepository profileRepository;
    private final AuthCodeRepository authCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${AUTH_CODE_EXPIRE_TIME}")
    private long AUTH_CODE_EXPIRE_TIME;

    // JWT 토큰 생성 후 Refresh Token 저장
    private JwtInfo saveRefreshToken(String email) {
        JwtInfo jwtInfo = jwtProvider.generateToken(email);
        RefreshToken refreshToken = refreshTokenMapper.toRefreshToken(email, jwtInfo);
        refreshTokenRepository.save(refreshToken);

        return jwtInfo;
    }

    // 회원 가입
    public JwtInfo register(RegisterRequest request) {
        Optional<Profile> email = profileRepository.findByEmail(request.getEmail());
        if (email.isPresent()) {
            throw new RuntimeException("This email already exists. [email: " + request.getEmail() + "]");
        }

        Optional<Profile> username = profileRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new RuntimeException("This username already exists. [username: " + request.getUsername() + "]");
        }

        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Password too short");
        }

        if (!verifyEmail(request.getEmail(), request.getAuthCode())) {
            throw new RuntimeException("This email is not verified. [email: " + request.getEmail() + "]");
        }

        // 사용자 정보 저장
        Profile profile = profileMapper.toProfile(request);
        profile.setLastActive(LocalDateTime.now());
        profile.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        profile.setEmailVerified(true); // 어차피 인증번호 안 맞으면 가입 못 하는데 이걸 DB에 저장할 필요가 있나?

        profileRepository.save(profile);

        log.info("registered successfully [email: " + request.getEmail() + "]");

        // JWT 토큰 생성 후 Refresh Token 저장
        return saveRefreshToken(request.getEmail());
    }

    // 로그인
    public JwtInfo login(LoginRequest request) {
        Optional<Profile> profile = profileRepository.findByEmail(request.getId());
        if (profile.isEmpty()) {
            profile = profileRepository.findByUsername(request.getId());
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), profile.get().getHashedPassword())) {
            throw new RuntimeException("Invalid password");
        }

        log.info("login successfully [email: {}]", profile.get().getEmail());

        // JWT 토큰 생성 후 Refresh Token 저장
        return saveRefreshToken(request.getId());
    }

    @Transactional
    // 인증번호 생성 후 이메일 전송
    public ResponseEntity<?> sendCodeToEmail(EmailRequest email) {
        AuthCode authCode = createAuthCode(email);
        try {
            emailService.sendAuthCodeEmail(email.getEmail(), authCode.getAuthCode());
            log.info("Email sent successfully [email: {}]", email.getEmail());
            return ResponseEntity.ok().build();
        } catch (RuntimeException | MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to send email in sendCodeToEmail", e);
        }
    }

    // 인증번호 생성 및 저장
    public AuthCode createAuthCode(EmailRequest email) {
        String randomCode = generateRandomCode(6);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plus(AUTH_CODE_EXPIRE_TIME, ChronoUnit.MILLIS);

        AuthCode code = authCodeMapper.toAuthCode(email.getEmail(), randomCode);
        code.setIssuedAt(now);
        code.setExpiredAt(expired);

        return authCodeRepository.save(code);
    }

    // 인증번호 랜덤 생성
    private String generateRandomCode(Integer length) {
        String numbers = "0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            stringBuilder.append(numbers.charAt(index));
        }

        return stringBuilder.toString();
    }

    // 인증번호 검증
    private boolean verifyEmail(String email, String authCode) {
        return authCodeRepository.findByEmailAndAuthCode(email, authCode)
                .map(code -> code.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElse(false);

//        return profileRepository.findByEmail(email)
//                .map(profile -> {
//                    profile.setEmailVerified(isVerified);
//                    profileRepository.save(profile);
//                    return ResponseEntity.ok().build();
//                }).orElse(ResponseEntity.notFound().build());
    }

    // 만료된 인증번호 매일 자정마다 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void removeAuthCode() {
        authCodeRepository.deleteAll(authCodeRepository.findByExpiredAtBefore(LocalDateTime.now()));
        log.info("Expired Auth Code has been removed");
    }
}
