package com.example.snsbackend.domain.post;

import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.PostRepository;
import com.example.snsbackend.repository.QuotePostRepository;
import com.example.snsbackend.repository.RegularPostRepository;
import com.example.snsbackend.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final RegularPostRepository regularPostRepository;
    private final QuotePostRepository quotePostRepository;
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;

    public Post getPost(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void createPost(PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        RegularPost newPost = new RegularPost();
        newPost.setType("post");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setCreatedAt(new Date());
        newPost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        newPost.setStat(new Stat());

        regularPostRepository.save(newPost);
    }

    public void createQuote(String postId, PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        QuotePost newPost = new QuotePost();
        newPost.setType("quote");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setCreatedAt(new Date());
        newPost.setOriginal_post_id(postId);
        newPost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        newPost.setStat(new Stat());

        quotePostRepository.save(newPost);
    }

    public void createRepost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Repost repost = new Repost();
        repost.setType("repost");
        repost.setOriginal_post_id(postId);
        repost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        repost.setCreatedAt(new Date());

        repostRepository.save(repost);
    }
}
