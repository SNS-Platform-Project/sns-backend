package com.example.snsbackend.domain.user;

import com.example.snsbackend.dto.NewDataRequest;
import com.example.snsbackend.exception.ApiErrorType;
import com.example.snsbackend.exception.ApiException;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final ProfileRepository profileRepository;

    // username 중복 검사
    public boolean checkUsername(String username) {
        Optional<Profile> profile = profileRepository.findByUsername(username);
        if (profile.isPresent()) {
            log.info("This username already exits [username: {}]", username);
            return false;
        }
        return true;
    }

    // 이메일 중복 검사
    public boolean checkEmail(String email) {
        Optional<Profile> profile = profileRepository.findByEmail(email);
        if (profile.isPresent()) {
            log.info("This email already exits [email: {}]", email);
            return false;
        }
        return true;
    }

    // 프로필 조회 (user_id 사용)
    public Optional<Profile> getProfile(String userId) {
        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new RuntimeException("Profile not found [userId: " + userId + "]");
        }
        return profile;
    }

    // 프로필 조회 (token 사용)
    public Optional<Profile> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new RuntimeException("Profile not found [userId: " + userId + "]");
        }
        return profile;
    }

    // username 설정
    public void setUsername(NewDataRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> check = profileRepository.findByUsername(request.getNewData());
        if (check.isPresent()) {
            throw new ApiException(ApiErrorType.CONFLICT, "username: " + request.getNewData(), "이미 사용 중인 아이디입니다.");
        }

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setUsername(request.getNewData());
        profileRepository.save(profile.get());
    }
}
