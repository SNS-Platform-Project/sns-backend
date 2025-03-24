package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.CommentRequest;
import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.dto.PostResponse;
import com.example.snsbackend.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public Response<PostResponse> getPost(@PathVariable String postId) {
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

    @PostMapping("/{postId}/comments")
    public void createComment(@PathVariable String postId, @RequestBody CommentRequest request) { commentService.createComment(postId, request);}

    @PostMapping("/comments/{commentId}/like")
    public void likeComment(@PathVariable String commentId) {
        commentService.likeComment(commentId);
    }
}


