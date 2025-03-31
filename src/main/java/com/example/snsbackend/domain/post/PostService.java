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
    private final QuotePostRepository quotePostRepository;
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final PostLikeRepository postLikeRepository;
    private final ProfileRepository profileRepository;
    private final CountUpdater countUpdater;

    public PostResponse getPost(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("유효하지 않은 게시물 ID: {}", postId);
            return new NoSuchElementException("게시물 ID가 유효하지 않습니다.");
        });

        Profile profile = profileRepository.findById(post.getUserId()).orElseThrow(() -> {
            log.error("유효하지 않은 사용자 ID: {}", post.getUserId());
            return new NoSuchElementException("사용자 ID가 유효하지 않습니다.");
        });

        User user = new User(profile.getId(), profile.getUsername(), profile.getProfilePictureUrl());

        return new PostResponse(post, user);
    }

    public void createPost(PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        Post post = new Post(content);
        post.setUserId(userId);

        postRepository.save(post);
    }

    public void createQuote(String originalPostId, PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        if (!postRepository.existsById(originalPostId)) {
            log.warn("유효하지 않은 게시물 ID: {}", originalPostId);
            throw new NoSuchElementException("유효하지 않은 게시물 ID");
        }

        QuotePost post = new QuotePost(content);
        post.setUserId(userId);
        post.setOriginal_post_id(originalPostId);
        post.setType("quote");

        quotePostRepository.save(post);
    }

    public void createRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!postRepository.existsById(originalPostId)) {
            log.warn("유효하지 않은 게시물 ID: {}", originalPostId);
            throw  new NoSuchElementException("유효하지 않은 게시물 ID");
        }

        // 기존 게시글의 repost_count 수치 증가
        countUpdater.increment(originalPostId, "stat.repost_count", Post.class);

        // 리포스트 저장
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
            countUpdater.decrement(originalPostId, "stat.repost_count", Post.class);
        } else {
            log.warn("사용자 {}가 게시물 {}을 리포스트한 기록이 없습니다.", userDetails.getUserId(), originalPostId);
            throw new NoSuchElementException("사용자의 리포스트 기록이 없음");
        }
    }

    public void deletePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("유효하지 않은 게시물 ID: {}", postId);
            return new NoSuchElementException("유효하지 않은 게시물 ID");
        });
        if (post.getUserId().equals(userDetails.getUserId())) {
            // TODO : 앞으로 게시글에 관련된 컬렉션을 추가 구현할 시 삭제도 같이 구현되어야 함.

            postRepository.delete(post);
            deleteRelatedPost(postId);
        } else {
            log.warn("사용자 {}가 게시물 {}에 대한 접근 권한이 없습니다.", userDetails.getUserId(), postId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 리소스에 대한 접근 권한이 없습니다.");
        }
    }

    private void deleteRelatedPost(String postId) {
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), CommentLike.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Comment.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Repost.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), PostLike.class);
    }

    public void likePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        if (!postRepository.existsById(postId)) {
            log.warn("유효하지 않은 게시물 ID: {}", postId);
            throw new NoSuchElementException("유효하지 않은 게시물 ID");
        }

        postLikeRepository.save(new PostLike(postId, userId, new Date()));
        countUpdater.increment(postId, "stat.likes_count", Post.class);
    }

    public void undoLikePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userId)
                        .and("postId").is(postId)), PostLike.class);

        if (result.getDeletedCount() > 0) {
            // 기존 게시글의 likes_count 수치 감소
            countUpdater.decrement(postId, "stat.likes_count", Post.class);
        } else {
            log.warn("사용자 {}가 게시물 {}을 좋아요한 기록이 없습니다.", userId, postId);
            throw new NoSuchElementException("사용자의 게시물 좋아요 기록 없음");
        }
    }
}
