package com.example.snsbackend.domain.follow;

import com.example.snsbackend.model.Follower;
import com.example.snsbackend.model.Following;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class FollowController {
    private final FollowService followService;

    // 팔로우
    @PostMapping("/{user_id}/follow")
    public void follow(@PathVariable String user_id) {
        followService.follow(user_id);
    }

     // 언팔로우
     @PostMapping("/{user_id}/unfollow")
     public void unfollow(@PathVariable String user_id) {
        followService.unfollow(user_id);
    }

    // 팔로워 목록
    @GetMapping("/followers")
    public Follower follower() {
        return followService.follower();
    }

    // 팔로잉 목록
    @GetMapping("/followings")
    public Following following() {
        return followService.following();
    }
}
