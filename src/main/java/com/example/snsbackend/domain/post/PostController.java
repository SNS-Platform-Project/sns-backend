package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetail>> getPost(@PathVariable String postId) {
        return ApiResponse.success(postService.getPost(postId));
    }

    @PostMapping("/regular")
    public ResponseEntity<ApiResponse<String>> createPost(@Valid @RequestBody PostRequest content) {
        return ApiResponse.success(postService.createPost(content));
    }

    @PostMapping("/{originalPostId}/quote")
    public ResponseEntity<ApiResponse<String>> createQuotePost(@PathVariable String originalPostId, @RequestBody PostRequest content) {
        return ApiResponse.success(postService.createQuote(originalPostId, content));
    }
    @PostMapping("/{originalPostId}/repost")
    public ResponseEntity<ApiResponse<String>> createRepost(@PathVariable String originalPostId) {
        return ApiResponse.success(postService.createRepost(originalPostId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{originalPostId}/repost")
    public ResponseEntity<ApiResponse<Void>> undoRepost(@PathVariable String originalPostId) {
        postService.undoRepost(originalPostId);
        return ApiResponse.success();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable String postId) {
        try {
            postService.likePost(postId);
            return ApiResponse.success();
        } catch (DuplicateKeyException e) {
            return ApiResponse.conflict();
        }
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> undoLikePost(@PathVariable String postId) {
        postService.undoLikePost(postId);
        return ApiResponse.success();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable String postId, @RequestBody CommentRequest request) {
        commentService.createComment(postId, request);
        return ApiResponse.success();
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String postId, @ModelAttribute PageParam pageParam) {
        return ApiResponse.success(commentService.getComments(postId, pageParam));
    }

    @DeleteMapping("/images")
    public ResponseEntity<?> deleteImage(@Valid @RequestBody ImageRequest request) throws Exception {
        return ApiResponse.success(postService.deleteImage(request));
    }
}


