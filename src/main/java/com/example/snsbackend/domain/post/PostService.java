package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.PostResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.NoSuchElementException;

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

    public PostResponse getPost(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        Profile profile = profileRepository.findById(post.getUserId()).orElseThrow(NoSuchElementException::new);
        User user = new User(profile.getId(), profile.getUsername(), profile.getProfilePictureUrl());

        return new PostResponse(post, user);
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

    public void deletePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        if (post.getUserId().equals(userDetails.getUserId())) {
            // TODO : 앞으로 게시글에 관련된 컬렉션을 추가 구현할 시 삭제도 같이 구현되어야 함.
            postRepository.delete(post);
            mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), PostLike.class);
            mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Comment.class);
            mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), CommentLike.class);
            mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Repost.class);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 리소스에 대한 접근 권한이 없습니다.");
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
