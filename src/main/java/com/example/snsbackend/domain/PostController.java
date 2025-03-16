package com.example.snsbackend.domain;

import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable String postId) throws JsonProcessingException {
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
}
