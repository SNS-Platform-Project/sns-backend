package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            return ApiResponse.success(postService.getPost(postId));
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound();
        }
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
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable String postId) {
        try {
            postService.deletePost(postId);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound();
        } catch (ResponseStatusException e) {
            return ApiResponse.status(e.getStatusCode(), e.getReason(), null);
        }
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
            return ApiResponse.status(HttpStatus.NOT_FOUND, e.getMessage(), null);
        }
    }
}


