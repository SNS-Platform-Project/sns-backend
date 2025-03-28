package com.example.snsbackend.domain.feed;

import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.dto.PostResponse;
import lombok.RequiredArgsConstructor;
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
    public NoOffsetPage<PostResponse> getRecentFeeds(@ModelAttribute PageParam pageParam) {
        return feedService.getRecentFeeds(pageParam);
    }

    // 사용자 피드 조회
    @GetMapping()
    public NoOffsetPage<PostResponse> getUserFeeds(@ModelAttribute PageParam pageParam) {
        return feedService.getUserFeeds(pageParam);
    }
}
