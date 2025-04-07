package com.example.snsbackend.domain.feed;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final FeedService feedService;

    // 홈 피드 조회
    @GetMapping("/latest")
    public ResponseEntity<?> getRecentFeeds(@ModelAttribute PageParam pageParam) {
        return ApiResponse.success(feedService.getRecentFeeds(pageParam));
    }

    // 사용자 피드 조회
    @GetMapping()
    public ResponseEntity<?> getUserFeeds(@ModelAttribute PageParam pageParam) {
        return ApiResponse.success(feedService.getUserFeeds(pageParam));
    }
}
