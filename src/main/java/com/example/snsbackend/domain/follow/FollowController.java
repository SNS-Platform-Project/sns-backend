package com.example.snsbackend.domain.follow;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.model.Follower;
import com.example.snsbackend.model.Following;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class FollowController {
    private final FollowService followService;

    // 팔로우
    @PostMapping("/{user_id}/follow")
    public ResponseEntity<?> follow(@PathVariable String user_id) {
        followService.follow(user_id);
        return ApiResponse.success();
    }

    // 언팔로우
    @DeleteMapping("/{user_id}/unfollow")
    public ResponseEntity<?> unfollow(@PathVariable String user_id) {
        followService.unfollow(user_id);
        return ApiResponse.success();
    }

    // 팔로우 요청 수락
    @PostMapping("/follow-requests/{user_id}/accept")
    public ResponseEntity<?> acceptFollowRequest(@PathVariable String user_id) {
        followService.acceptFollowRequest(user_id);
        return ApiResponse.success();
    }

    // 팔로우 요청 삭제
    @DeleteMapping("/follow-requests/{user_id}/reject")
    public ResponseEntity<?> rejectFollowRequest(@PathVariable String user_id) {
        followService.rejectFollowRequest(user_id);
        return ApiResponse.success();
    }

    // 팔로워 목록
    @GetMapping("/followers")
    public ResponseEntity<?> follower() {
        return ApiResponse.success(followService.follower());
    }

    // 팔로잉 목록
    @GetMapping("/followings")
    public ResponseEntity<?> following() {
        return ApiResponse.success(followService.following());
    }

    // 받은 팔로우 요청 목록
    @GetMapping("/follow-requests/received")
    public ResponseEntity<?> ReceivedFollowRequest() {
        return ApiResponse.success(followService.ReceivedFollowRequest());
    }

    // 보낸 팔로우 요청 목록
    @GetMapping("/follow-requests/sent")
    public ResponseEntity<?> SentFollowRequest() {
        return ApiResponse.success(followService.SentFollowRequest());
    }
}
