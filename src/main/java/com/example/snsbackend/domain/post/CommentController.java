package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.PageParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable String commentId) {
        try {
            commentService.likeComment(commentId);
            return ApiResponse.success();

        } catch (DuplicateKeyException e) {
            return ApiResponse.conflict();
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> undoLikeComment(@PathVariable String commentId) {
        try {
            commentService.undoLikeComment(commentId);
            return ApiResponse.success();

        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable String commentId, @ModelAttribute PageParam pageParam) {
        try {
            return ApiResponse.success(commentService.getReplies(commentId, pageParam));
        } catch (NoSuchElementException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }
}
