package com.example.snsbackend.auth;

import com.example.snsbackend.dto.EmailRequest;
import com.example.snsbackend.email.EmailService;
import com.example.snsbackend.model.AuthCode;
import com.example.snsbackend.repository.AuthCodeRepository;
import com.example.snsbackend.repository.ProfileRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ProfileRepository profileRepository;
    private final AuthCodeRepository authCodeRepository;
    private final EmailService emailService;

    @Value("${AUTH_CODE_EXPIRE_TIME}")
    private long AUTH_CODE_EXPIRE_TIME;

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
        System.out.println(LocalDateTime.now());
        AuthCode code = AuthCode.builder()
                .email(email.getEmail())
                .authCode(randomCode)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plus(AUTH_CODE_EXPIRE_TIME, ChronoUnit.MILLIS))
                .build();

        return authCodeRepository.save(code);
    }

    // 인증번호 랜덤 생성
    public String generateRandomCode(Integer length) {
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
    public ResponseEntity<?> verifyEmail(String email, String authCode) {
        boolean isVerified =  authCodeRepository.findByEmailAndAuthCode(email, authCode)
                .map(code -> code.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElse(false);

        return profileRepository.findByEmail(email)
                .map(profile -> {
                    profile.setEmailVerified(isVerified);
                    profileRepository.save(profile);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    // 만료된 인증번호 매일 자정마다 삭제
    @Scheduled(cron = "0 * * * * *")
    public void removeAuthCode() {
        authCodeRepository.deleteAll(authCodeRepository.findByExpiredAtBefore(LocalDateTime.now()));
        log.info("Expired Auth Code has been removed");
    }
}
