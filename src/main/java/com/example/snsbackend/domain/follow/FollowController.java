package com.example.snsbackend.domain.follow;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class FollowController {
    private final FollowService followService;

    // 팔로우
    @PostMapping("/{username}/follow")
    public void follow(@PathVariable String username) {
        followService.follow(username);
    }

     // 언팔로우
     @PostMapping("/{username}/unfollow")
     public void unfollow(@PathVariable String username) {
        followService.unfollow(username);
    }
}
