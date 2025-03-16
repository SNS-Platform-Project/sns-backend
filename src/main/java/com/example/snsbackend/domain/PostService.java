package com.example.snsbackend.domain;

import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.PostRepository;
import com.example.snsbackend.repository.QuotePostRepository;
import com.example.snsbackend.repository.RegularPostRepository;
import com.example.snsbackend.repository.RepostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    @Autowired
    private MongoTemplate mongoTemplate;

    private final RegularPostRepository regularPostRepository;
    private final QuotePostRepository quotePostRepository;
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;

    public Post getPost(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void createPost(PostRequest content) {
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String userId = authentication.getName();
        // 인증객체에서 유저정보 가져와서 넣어야함

        RegularPost newPost = new RegularPost();
        newPost.setType("post");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setCreatedAt(new Date());
        newPost.setUser(new User("test", "test"));
        newPost.setStat(new Stat());

        regularPostRepository.save(newPost);
    }

    public void createQuote(String postId, PostRequest content) {
        QuotePost newPost = new QuotePost();
        newPost.setType("quote");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setCreatedAt(new Date());
        newPost.setOriginal_post_id(postId);
        newPost.setUser(new User("test", "test"));
        newPost.setStat(new Stat());

        quotePostRepository.save(newPost);
    }

    public void createRepost(String postId) {
        Repost repost = new Repost();
        repost.setType("repost");
        repost.setOriginal_post_id(postId);
        repost.setUser(new User("test", "test"));
        repost.setCreatedAt(new Date());

        repostRepository.save(repost);
    }
}
