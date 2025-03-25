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
    private final CountUpdater countUpdater;

    public PostResponse getPost(String postId) {
        log.info("사용자가 게시물 {}를 요청합니다.", postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("유효하지 않은 게시물 ID: {}", postId);
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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("사용자 {}가 게시물 작성을 시도합니다.", userDetails.getUserId());

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

        // 게시글 내용 요약 (최대 50자)
        String summary = content.getContent().length() > 50 ? content.getContent().substring(0, 50) + "..." : content.getContent();

        log.info("사용자 {}가 게시물을 성공적으로 작성했습니다. 게시물 내용 : {}", userDetails.getUserId(), summary);
    }

    public void createQuote(String originalPostId, PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("사용자 {}가 게시글 {}을 인용한 게시글을 작성하려고 합니다.", userDetails.getUserId(), originalPostId);

        if (!postRepository.existsById(originalPostId)) {
            log.error("유효하지 않은 게시물 ID: {}", originalPostId);
            throw new NoSuchElementException();
        }

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

        // 게시글 내용 요약 (최대 50자)
        String summary = content.getContent().length() > 50 ? content.getContent().substring(0, 50) + "..." : content.getContent();

        log.info("사용자 {}가 인용 게시물을 성공적으로 작성했습니다. 게시물 내용 : {}", userDetails.getUserId(), summary);
    }

    public void createRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("사용자 {}가 게시물 {}를 리포스트하려고 합니다.", userDetails.getUserId(), originalPostId);

        // 기존 게시글의 repost_count 수치 증가
        countUpdater.incrementCount(originalPostId, "stat.repost_count", Post.class);

        // 리포스트 저장
        repostRepository.save(new Repost(userDetails.getUserId(), originalPostId, new Date()));

        log.info("사용자 {}가 게시물 {}를 성공적으로 리포스트했습니다.", userDetails.getUserId(), originalPostId);
    }

    public void undoRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("사용자 {}가 게시물 {}의 리포스트를 취소하려고 합니다.", userDetails.getUserId(), originalPostId);

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userDetails.getUserId())
                        .and("postId").is(originalPostId)), Repost.class);

        if (result.getDeletedCount() > 0) {
            // 기존 게시글의 repost_count 수치 감소
            countUpdater.decrementCount(originalPostId, "stat.repost_count", Post.class);
            log.info("사용자 {}가 게시물 {}의 리포스트를 성공적으로 취소했습니다.", userDetails.getUserId(), originalPostId);
        } else {
            log.error("사용자 {}가 게시물 {}을 리포스트한 기록이 없습니다.", userDetails.getUserId(), originalPostId);
            throw new NoSuchElementException();
        }
    }

    public void deletePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("유효하지 않은 게시물 ID: {}", postId);
            return new NoSuchElementException();
        });
        if (post.getUserId().equals(userDetails.getUserId())) {
            log.info("사용자 {}가 게시물 {}를 삭제합니다.", userDetails.getUserId(), postId);
            // TODO : 앞으로 게시글에 관련된 컬렉션을 추가 구현할 시 삭제도 같이 구현되어야 함.

            postRepository.delete(post);
            deleteRelatedPost(postId);

            log.info("게시물 {}와 관련된 모든 데이터를 삭제했습니다.", postId);
        } else {
            log.error("사용자 {}가 게시물 {}에 대한 접근 권한이 없습니다.", userDetails.getUserId(), postId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 리소스에 대한 접근 권한이 없습니다.");
        }
    }

    private void deleteRelatedPost(String postId) {
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), PostLike.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Comment.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), CommentLike.class);
        mongoTemplate.remove(new Query(Criteria.where("postId").is(postId)), Repost.class);
    }

    public void likePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        log.info("사용자 {}가 게시물 {}에 좋아요를 추가합니다.", userId, postId);

        postLikeRepository.save(new PostLike(postId, userId, new Date()));
        countUpdater.incrementCount(postId, "stat.likes_count", Post.class);

        log.info("사용자 {}가 게시물 {}에 좋아요를 성공적으로 추가했습니다.", userId, postId);
    }

    public void undoLikePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        log.info("사용자 {}가 게시물 {}의 좋아요를 취소하려고 합니다.", userId, postId);

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userId)
                        .and("postId").is(postId)), PostLike.class);

        if (result.getDeletedCount() > 0) {
            // 기존 게시글의 likes_count 수치 감소
            countUpdater.decrementCount(postId, "stat.likes_count", Post.class);
            log.info("사용자 {}가 게시물 {}의 좋아요를 성공적으로 취소했습니다.", userId, postId);
        } else {
            log.error("사용자 {}가 게시물 {}을 좋아요한 기록이 없습니다.", userId, postId);
            throw new NoSuchElementException();
        }
    }
}
