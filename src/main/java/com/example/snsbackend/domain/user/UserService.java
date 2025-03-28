package com.example.snsbackend.domain.user;

import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 프로필 조회
    public Optional<Profile> getProfile(String userId) {
        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new RuntimeException("Profile not found [userId: " + userId + "]");
        }
        return profile;
    }
}
