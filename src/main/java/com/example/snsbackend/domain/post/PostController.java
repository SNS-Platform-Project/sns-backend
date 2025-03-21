package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/{originalPostId}/quote")
    public void createQuotePost(@PathVariable String originalPostId, @RequestBody PostRequest content) {
        postService.createQuote(originalPostId, content);
    }
    @PostMapping("/{originalPostId}/repost")
    public void createRepost(@PathVariable String originalPostId) {
        postService.createRepost(originalPostId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable String postId) throws Exception {
        postService.deletePost(postId);
    }

    @DeleteMapping("/{originalPostId}/repost")
    public void undoRepost(@PathVariable String originalPostId) {
        postService.undoRepost(originalPostId);
    }

    @PostMapping("/{postId}/like")
    public void likePost(@PathVariable String postId) {
        postService.likePost(postId);
    }

    @DeleteMapping("/{postId}/like")
    public void undoLikePost(@PathVariable String postId) {
        postService.undoLikePost(postId);
    }
}


