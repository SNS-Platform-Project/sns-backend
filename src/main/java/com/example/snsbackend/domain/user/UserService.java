package com.example.snsbackend.domain.user;

import com.example.snsbackend.dto.BirthdayRequest;
import com.example.snsbackend.dto.NewDataRequest;
import com.example.snsbackend.dto.ProfilePictureRequest;
import com.example.snsbackend.exception.ApiErrorType;
import com.example.snsbackend.exception.ApiException;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.Image;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final ProfileRepository profileRepository;

    private final Pattern USERNAME_PATTERN = Pattern.compile("^(?!^\\.)(?!.*\\.$)(?!.*\\.\\.)(?=.{3,30}$)[a-z0-9._]+$");

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

        if (!USERNAME_PATTERN.matcher(request.getNewData()).matches()) {
            throw new ApiException(ApiErrorType.INVALID_USERNAME, "username: " + request.getNewData());
        }

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setUsername(request.getNewData());
        profileRepository.save(profile.get());
    }

    // 자기소개 메시지 설정
    public void setBio(NewDataRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        if (request.getNewData().length() > 300) {
            throw new ApiException(ApiErrorType.BAD_REQUEST, null,"최대 300자까지 입력할 수 있습니다.");
        }

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setBio(request.getNewData());
        profileRepository.save(profile.get());
    }

    // 계정 공개 여부 설정
    public void setPublic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setPublic(!profile.get().isPublic());
        profileRepository.save(profile.get());
    }

    // 생일 설정
    public void setBirthday(BirthdayRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setBirthday(request.getBirthday());
        profileRepository.save(profile.get());
    }

    // 프로필 외부 링크 설정
    public void setSocialLinks(NewDataRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setSocialLinks(request.getNewData());
        profileRepository.save(profile.get());
    }

    // 프로필 사진 업로드
    public void setProfilePicture(ProfilePictureRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        profile.get().setProfilePictureUrl(request.getImage());
        profileRepository.save(profile.get());
    }

    // 프로필 사진 조회
    public Image getImage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Optional<Profile> profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다.");
        }

        return profile.get().getProfilePictureUrl();
    }
}
