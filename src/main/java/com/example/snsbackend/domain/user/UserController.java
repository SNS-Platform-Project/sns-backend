package com.example.snsbackend.domain.user;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.BirthdayRequest;
import com.example.snsbackend.dto.NewDataRequest;
import com.example.snsbackend.dto.ProfilePictureRequest;
import com.example.snsbackend.model.Profile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    // username 중복 검사
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username){
        return ApiResponse.success(userService.checkUsername(username));
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email){
        return ApiResponse.success(userService.checkEmail(email));
    }

    // 프로필 조회 (user_id 사용)
    @GetMapping("/{user_id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String user_id) {
        return ApiResponse.success(userService.getProfile(user_id));
    }

    // 프로필 조회 (token 사용)
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile() {
        return ApiResponse.success(userService.getMyProfile());
    }

    // username 설정
    @PatchMapping("/profile/username")
    public ResponseEntity<?> setUsername(@RequestBody @Valid NewDataRequest request) {
        userService.setUsername(request);
        return ApiResponse.success();
    }

    // 자기소개 메시지 설정
    @PatchMapping("/profile/bio")
    public ResponseEntity<?> setBio(@RequestBody @Valid NewDataRequest request) {
        userService.setBio(request);
        return ApiResponse.success();
    }

    // 계정 공개 여부 설정
    @PatchMapping("/profile/privacy")
    public ResponseEntity<?> setPrivacy() {
        userService.setPrivacy();
        return ApiResponse.success();
    }

    // 생일 설정
    @PatchMapping("/profile/birthday")
    public ResponseEntity<?> setBirthday(@RequestBody @Valid BirthdayRequest request) {
        userService.setBirthday(request);
        return ApiResponse.success();
    }

    // 프로필 외부 링크 설정
    @PatchMapping("/profile/link")
    public ResponseEntity<?> setLink(@RequestBody @Valid NewDataRequest request) {
        userService.setSocialLinks(request);
        return ApiResponse.success();
    }

    // 프로필 사진 업로드
    @PatchMapping("/profile/photo")
    public ResponseEntity<?> setPhoto(@RequestBody @Valid ProfilePictureRequest request) {
        userService.setProfilePicture(request);
        return ApiResponse.success();
    }

    // 프로필 사진 조회
    @GetMapping("/profile/photo")
    public ResponseEntity<?> getPhoto() {
        return ApiResponse.success(userService.getImage());
    }
}
