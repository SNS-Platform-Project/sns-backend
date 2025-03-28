package com.example.snsbackend.domain.auth;

import com.example.snsbackend.dto.AuthCodeRequest;
import com.example.snsbackend.dto.EmailRequest;
import com.example.snsbackend.dto.LoginRequest;
import com.example.snsbackend.dto.RegisterRequest;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.jwt.JwtInfo;
import com.example.snsbackend.jwt.JwtProvider;
import com.example.snsbackend.jwt.TokenUtils;
import com.example.snsbackend.mapper.AccessTokenBlackListMapper;
import com.example.snsbackend.mapper.AuthCodeMapper;
import com.example.snsbackend.mapper.ProfileMapper;
import com.example.snsbackend.mapper.RefreshTokenMapper;
import com.example.snsbackend.model.AccessTokenBlackList;
import com.example.snsbackend.model.AuthCode;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.model.RefreshToken;
import com.example.snsbackend.repository.AccessTokenBlackListRepository;
import com.example.snsbackend.repository.AuthCodeRepository;
import com.example.snsbackend.repository.ProfileRepository;
import com.example.snsbackend.repository.RefreshTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthCodeMapper authCodeMapper;
    private final ProfileMapper profileMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final AccessTokenBlackListMapper accessTokenBlackListMapper;
    private final ProfileRepository profileRepository;
    private final AuthCodeRepository authCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${AUTH_CODE_EXPIRE_TIME}")
    private long AUTH_CODE_EXPIRE_TIME;

    private final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,256})([a-zA-Z0-9._%+-]{1,64})@((?:(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|(?:\\d{1,3}\\.){3}\\d{1,3}))$");
    private final Pattern USERNAME_PATTERN = Pattern.compile("^(?!^\\.)(?!.*\\.$)(?!.*\\.\\.)(?=.{3,30}$)[a-z0-9._]+$");

    // JWT 토큰 생성 후 Refresh Token 저장
    private JwtInfo saveRefreshToken(String userId) {
        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        JwtInfo jwtInfo = jwtProvider.generateToken(userId);
        RefreshToken refreshToken = refreshTokenMapper.toRefreshToken(userId, jwtInfo);
        refreshTokenRepository.save(refreshToken);

        return jwtInfo;
    }

    // 회원 가입
    public JwtInfo register(RegisterRequest request) {
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Invalid email format. [email: " + request.getEmail() + "]");
        }

        Optional<Profile> email = profileRepository.findByEmail(request.getEmail());
        if (email.isPresent()) {
            throw new RuntimeException("This email already exists. [email: " + request.getEmail() + "]");
        }

        if (!USERNAME_PATTERN.matcher(request.getUsername()).matches()) {
            throw new RuntimeException("Invalid username format. [username: " + request.getUsername() + "]");
        }

        Optional<Profile> username = profileRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new RuntimeException("This username already exists. [username: " + request.getUsername() + "]");
        }

        Optional<AuthCode> authCode = authCodeRepository.findByEmail(request.getEmail());
        authCode.ifPresentOrElse(code -> {
            if (!code.isEmailVerified()) {
                throw new RuntimeException("This email is not verified. [email: " + request.getEmail() + "]");
            }
        }, () -> {
            throw new RuntimeException("No Auth Code found. [email: " + request.getEmail() + "]");
        });

        // 사용자 정보 저장
        Profile profile = profileMapper.toProfile(request);
        profile.setLastActive(LocalDateTime.now());
        profile.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        profileRepository.save(profile);

        log.info("registered successfully [userid: " + profile.getId() + "]");

        // JWT 토큰 생성 후 Refresh Token 저장
        return saveRefreshToken(profile.getId());
    }

    // 로그인
    public JwtInfo login(LoginRequest request) {
        // AuthenticationManager은 AuthenticationProvider들을 순회하며 UserDetailsService를 호출한다.
        // 이때 CustomUserDetailsService를 새로 Service에 등록했기 때문에 커스텀된 구현체가 인식되며,
        // overriding 된 loadUserByUsername 함수가 호출된다.
        // UserDetails에 있는 사용자 비밀번호와 입력된 비밀번호를 비교하여 인증에 성공하면 Authentication 객체가 반환됨

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("login successfully [userID: {}]", userDetails.getUserId());

        // JWT 토큰 생성 후 Refresh Token 저장
        return saveRefreshToken(userDetails.getUserId());
    }

    // 로그아웃
    public void logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();
        String accessToken = TokenUtils.extractAccessTokenFromHeader(request);

        // 블랙리스트에 Access Token 추가
        AccessTokenBlackList accessTokenBlackList = accessTokenBlackListMapper.toAccessTokenBlackList(accessToken);
        accessTokenBlackListRepository.save(accessTokenBlackList);

        // Refresh Token 삭제
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(userId);
        refreshToken.ifPresentOrElse(token -> {
            refreshTokenRepository.deleteByUserId(userId);
        }, () -> {
            throw new RuntimeException("No RefreshToken found. [userId: " + userId + "]");
        });

        log.info("logout successfully [userID: {}]", userId);
    }

    // 블랙리스트에 있는 만료된 토큰 삭제
    @Scheduled(cron = "0 0 0/1 * * *")
    public void removeAccessTokenBlackList() {
        accessTokenBlackListRepository.deleteAll(accessTokenBlackListRepository.findByBlackAtBefore(LocalDateTime.now().minusHours(1)));
        log.info("Expired token has been removed");
    }

    @Transactional
    // 인증번호 생성 후 이메일 전송
    public ResponseEntity<?> sendCodeToEmail(EmailRequest email) {
        AuthCode authCode = createAuthCode(email.getEmail());
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
    private AuthCode createAuthCode(String email) {
        String randomCode = generateRandomCode(6);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plus(AUTH_CODE_EXPIRE_TIME, ChronoUnit.MILLIS);

        AuthCode code = authCodeMapper.toAuthCode(email, randomCode);
        code.setIssuedAt(now);
        code.setExpiredAt(expired);

        // 이전의 인증번호 삭제
        authCodeRepository.deleteByEmail(email);

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
    public ResponseEntity<?> verifyEmail(AuthCodeRequest request) {
        boolean isVerified = authCodeRepository.findByEmailAndAuthCode(request.getEmail(), request.getAuthCode())
                .map(c -> c.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElse(false);

        if (!isVerified) {
            throw new RuntimeException("Invalid email verification code. [email: " + request.getEmail() + "]");
        }

        Optional<AuthCode> authCode = authCodeRepository.findByEmail(request.getEmail());
        authCode.ifPresent(code -> {
            code.setEmailVerified(isVerified);
            authCodeRepository.save(code);
        });
        return ResponseEntity.ok().build();
    }

    // 만료된 인증번호 삭제
    @Scheduled(cron = "0 0 0/1 * * *")
    public void removeAuthCode() {
        authCodeRepository.deleteAll(authCodeRepository.findByExpiredAtBefore(LocalDateTime.now()));
        log.info("Expired Auth Code has been removed");
    }
}
