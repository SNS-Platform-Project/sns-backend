package com.example.snsbackend.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
