package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
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
            return ApiResponse.notFound(e.getMessage());
        }
    }

    @PostMapping("/regular")
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostRequest content) {
        postService.createPost(content);
        return ApiResponse.success();
    }

    @PostMapping("/{originalPostId}/quote")
    public ResponseEntity<ApiResponse<Void>> createQuotePost(@PathVariable String originalPostId, @RequestBody PostRequest content) {
        try {
            postService.createQuote(originalPostId, content);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }
    @PostMapping("/{originalPostId}/repost")
    public ResponseEntity<ApiResponse<Void>> createRepost(@PathVariable String originalPostId) {
        try {
            postService.createRepost(originalPostId);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
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
    public ResponseEntity<ApiResponse<Void>> undoRepost(@PathVariable String originalPostId) {
        try {
            postService.undoRepost(originalPostId);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable String postId) {
        try {
            postService.likePost(postId);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        } catch (DuplicateKeyException e) {
            return ApiResponse.conflict();
        }
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> undoLikePost(@PathVariable String postId) {
        try {
            postService.undoLikePost(postId);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable String postId, @RequestBody CommentRequest request) {
        try {
            commentService.createComment(postId, request);
            return ApiResponse.success();
        } catch (NoSuchElementException e) {
            return ApiResponse.status(HttpStatus.NOT_FOUND, e.getMessage(), null);
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId, @ModelAttribute PageParam pageParam) {
        try {
            return ApiResponse.success(commentService.getComments(postId, pageParam));
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }
}


