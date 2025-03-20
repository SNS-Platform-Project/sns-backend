package com.example.snsbackend.post;

import com.example.snsbackend.auth.CustomUserDetails;
import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable String postId) {
        return postService.getPost(postId);
    }

    @PostMapping("/regular")
    public void createPost(@RequestBody PostRequest content) {
        postService.createPost(content);
    }

    @PostMapping("/{postId}/quote")
    public void createQuotePost(@PathVariable String postId, @RequestBody PostRequest content) {
        postService.createQuote(postId, content);
    }
    @PostMapping("/{postId}/repost")
    public void createRePost(@PathVariable String postId) {
        postService.createRepost(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable String postId) {

    }
}


