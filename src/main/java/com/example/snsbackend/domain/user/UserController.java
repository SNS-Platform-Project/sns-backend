package com.example.snsbackend.domain.user;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.NewDataRequest;
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
    public boolean checkUsername(@RequestParam("username") String username){
        return userService.checkUsername(username);
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam("email") String email){
        return userService.checkEmail(email);
    }

    // 프로필 조회 (user_id 사용)
    @GetMapping("/{user_id}/profile")
    public Optional<Profile> getProfile(@PathVariable String user_id) {
        return userService.getProfile(user_id);
    }

    // 프로필 조회 (token 사용)
    @GetMapping("/profile")
    public Optional<Profile> getMyProfile() {
        return userService.getMyProfile();
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
}
