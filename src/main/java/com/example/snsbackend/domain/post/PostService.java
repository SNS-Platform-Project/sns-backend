package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.PostResponse;
import com.example.snsbackend.dto.Response;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.*;
import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final RegularPostRepository regularPostRepository;
    private final QuotePostRepository quotePostRepository;
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final PostLikeRepository postLikeRepository;
    private final ProfileRepository profileRepository;

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
        newPost.setImages(content.getImages());
        newPost.setCreatedAt(new Date());
        newPost.setUserId(userDetails.getUserId());
        newPost.setStat(new Stat());

        regularPostRepository.save(newPost);
    }

    public void createQuote(String originalPostId, PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        QuotePost newPost = new QuotePost();
        newPost.setType("quote");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setImages(content.getImages());
        newPost.setCreatedAt(new Date());
        newPost.setOriginal_post_id(originalPostId);
        newPost.setUserId(userDetails.getUserId());
        newPost.setStat(new Stat());

        quotePostRepository.save(newPost);
    }

    public void createRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 기존 게시글의 repost_count 수치 증가
        postStatUpdate(originalPostId, "repost_count", 1);

        repostRepository.save(new Repost(userDetails.getUserId(), originalPostId, new Date()));
    }

    public void undoRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userDetails.getUserId())
                        .and("postId").is(originalPostId)), Repost.class);

        if (result.getDeletedCount() > 0) {
            // 기존 게시글의 repost_count 수치 감소
            postStatUpdate(originalPostId, "repost_count", -1);
        }
    }

    public void deletePost(String postId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception("not found"));
        if (post.getUserId().equals(userDetails.getUserId())) {
            postRepository.delete(post);
        } else {
            throw new Exception("Unauthorized");
        }
    }

    public void likePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        postLikeRepository.save(new PostLike(postId, userId, new Date()));
        postStatUpdate(postId, "likes_count", 1);
    }

    public void undoLikePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userId)
                        .and("postId").is(postId)), PostLike.class);

        if (result.getDeletedCount() > 0) {
            // 기존 게시글의 likes_count 수치 감소
            postStatUpdate(postId, "likes_count", -1);
        }
    }

    private void postStatUpdate(String postId, String fieldName, Number inc) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("id").is(postId)),
                new Update().inc(String.format("stat.%s", fieldName), inc),
                Post.class);
    }
}
