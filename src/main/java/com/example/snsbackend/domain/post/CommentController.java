package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
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
            return ApiResponse.notFound();
        }
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> undoLikeComment(@PathVariable String commentId) {
        try {
            commentService.undoLikeComment(commentId);
            return ApiResponse.success();

        } catch (NoSuchElementException e) {
            return ApiResponse.notFound();
        }
    }
}
