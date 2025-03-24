package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable String postId) {
        return ApiResponse.success(postService.getPost(postId));
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

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable String postId, @RequestBody CommentRequest request) {
        try {
            commentService.createComment(postId, request);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound();
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId, @ModelAttribute PageParam pageParam) {
        try {
            return ApiResponse.success(commentService.getComments(postId, pageParam));
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound();
        }
    }
}


