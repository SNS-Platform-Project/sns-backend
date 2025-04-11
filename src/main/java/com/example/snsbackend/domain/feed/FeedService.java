package com.example.snsbackend.domain.feed;

import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.dto.PostResponse;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.*;
import com.example.snsbackend.model.post.Post;
import com.example.snsbackend.model.post.Repost;
import com.example.snsbackend.repository.FollowingRepository;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FollowingRepository followingRepository;
    private final MongoTemplate mongoTemplate;
    private final ProfileRepository profileRepository;

    // 홈 피드 조회
    public NoOffsetPage<PostResponse> getRecentFeeds(PageParam pageParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 팔로우 중인 사용자 + 자기 자신
        Following following = followingRepository.findByUserId(userId);
        List<String> follow = new ArrayList<>();
        if (following != null) {
            follow = following.getFollowings().stream().map(Follow::getFollowId).collect(Collectors.toList());
        }
        follow.add(userId);

        // 쿼리
        Query query = new Query(Criteria.where("user_id").in(follow));

        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 게시물 ID: {} 이후의 게시물을 조회합니다.", pageParam.getLastId());
        }

        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        // 최신순 게시글
        List<Post> posts = mongoTemplate.find(query, Post.class);

        // 최신순 리포스트
        List<Repost> reposts = mongoTemplate.find(query, Repost.class);

        // 하나의 타임라인으로 병합
        List<Timeline> timeline = new ArrayList<>();
        posts.forEach(post -> timeline.add(new Timeline(post.getCreatedAt(), post, null)));
        reposts.forEach(repost -> {
            Post originalPost = mongoTemplate.findById(repost.getPostId(), Post.class);
            if (originalPost != null) {
                timeline.add(new Timeline(repost.getCreatedAt(), originalPost, repost.getUserId()));
            }
        });

        // 최신순 정렬
        timeline.sort(Comparator.comparing(Timeline::getCreatedAt).reversed());

        // 페이징
        List<Timeline> pagedTimeline = timeline.stream().limit(pageParam.getSize()).collect(Collectors.toList());

        List<PostResponse> responses = pagedTimeline.stream().map(item -> {
            Optional<Profile> profile = profileRepository.findById(item.getPost().getUserId());
            if (profile.isEmpty()) throw new RuntimeException("Profile not found");

            User user = new User(profile.get().getId(), profile.get().getUsername(), profile.get().getProfilePictureUrl());

            User repostedBy = null;
            if (item.getRepostedByUserId() != null) {
                Optional<Profile> reProfile = profileRepository.findById(item.getRepostedByUserId());
                if (reProfile.isPresent()) {
                    repostedBy = new User(reProfile.get().getId(), reProfile.get().getUsername(), reProfile.get().getProfilePictureUrl());
                }
            }

            return new PostResponse(item.getPost(), user, repostedBy);
        }).collect(Collectors.toList());

        String lastId = responses.isEmpty() ? null : responses.get(responses.size() - 1).getPost().getId();
        return new NoOffsetPage<>(responses, lastId, pageParam.getSize());
    }

    public NoOffsetPage<PostResponse> getRecentFeeds2(PageParam pageParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 팔로우 중인 사용자 + 자기 자신
        Following following = followingRepository.findByUserId(userId);
        List<String> follow = new ArrayList<>();
        if (following != null) {
            follow = following.getFollowings().stream().map(Follow::getFollowId).collect(Collectors.toList());
        }
        follow.add(userId);

        // 쿼리
        Query query = new Query(Criteria.where("user_id").in(follow));

        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 게시물 ID: {} 이후의 게시물을 조회합니다.", pageParam.getLastId());
        }

        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        // 최신순 게시글
        List<Post> posts = mongoTemplate.find(query, Post.class);

        // 최신순 리포스트
        List<Repost> reposts = mongoTemplate.find(query, Repost.class);

        // 하나의 타임라인으로 병합
        List<Timeline> timeline = new ArrayList<>();
        posts.forEach(post -> timeline.add(new Timeline(post.getCreatedAt(), post, null)));
        reposts.forEach(repost -> {
            Post originalPost = mongoTemplate.findById(repost.getPostId(), Post.class);
            if (originalPost != null) {
                timeline.add(new Timeline(repost.getCreatedAt(), originalPost, repost.getUserId()));
            }
        });

        // 최신순 정렬
        timeline.sort(Comparator.comparing(Timeline::getCreatedAt).reversed());

        // 페이징
        List<Timeline> pagedTimeline = timeline.stream().limit(pageParam.getSize()).collect(Collectors.toList());

        List<PostResponse> responses = pagedTimeline.stream().map(item -> {
            Optional<Profile> profile = profileRepository.findById(item.getPost().getUserId());
            if (profile.isEmpty()) throw new RuntimeException("Profile not found");

            User user = new User(profile.get().getId(), profile.get().getUsername(), profile.get().getProfilePictureUrl());

            User repostedBy = null;
            if (item.getRepostedByUserId() != null) {
                Optional<Profile> reProfile = profileRepository.findById(item.getRepostedByUserId());
                if (reProfile.isPresent()) {
                    repostedBy = new User(reProfile.get().getId(), reProfile.get().getUsername(), reProfile.get().getProfilePictureUrl());
                }
            }

            return new PostResponse(item.getPost(), user, repostedBy);
        }).collect(Collectors.toList());

        String lastId = responses.isEmpty() ? null : responses.get(responses.size() - 1).getPost().getId();
        return new NoOffsetPage<>(responses, lastId, pageParam.getSize());
    }

    // 사용자 피드 조회
    public NoOffsetPage<PostResponse> getUserFeeds(PageParam pageParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 쿼리
        Query query = new Query().addCriteria(Criteria.where("user_id").is(userId));

        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 게시물 ID: {} 이후의 게시물을 조회합니다.", pageParam.getLastId());
        }

        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        // 최신순 게시글
        List<Post> posts = mongoTemplate.find(query, Post.class);

        // 최신순 리포스트
        List<Repost> reposts = mongoTemplate.find(query, Repost.class);

        // 하나의 타임라인으로 병합
        List<Timeline> timeline = new ArrayList<>();
        posts.forEach(post -> timeline.add(new Timeline(post.getCreatedAt(), post, null)));
        reposts.forEach(repost -> {
            Post originalPost = mongoTemplate.findById(repost.getPostId(), Post.class);
            if (originalPost != null) {
                timeline.add(new Timeline(repost.getCreatedAt(), originalPost, repost.getUserId()));
            }
        });

        // 최신순 정렬
        timeline.sort(Comparator.comparing(Timeline::getCreatedAt).reversed());

        // 페이징
        List<Timeline> pagedTimeline = timeline.stream().limit(pageParam.getSize()).collect(Collectors.toList());

        List<PostResponse> responses = pagedTimeline.stream().map(item -> {
            Optional<Profile> profile = profileRepository.findById(item.getPost().getUserId());
            if (profile.isEmpty()) throw new RuntimeException("Profile not found");

            User user = new User(profile.get().getId(), profile.get().getUsername(), profile.get().getProfilePictureUrl());

            User repostedBy = null;
            if (item.getRepostedByUserId() != null) {
                Optional<Profile> reProfile = profileRepository.findById(item.getRepostedByUserId());
                if (reProfile.isPresent()) {
                    repostedBy = new User(reProfile.get().getId(), reProfile.get().getUsername(), reProfile.get().getProfilePictureUrl());
                }
            }

            return new PostResponse(item.getPost(), user, repostedBy);
        }).collect(Collectors.toList());

        String lastId = responses.isEmpty() ? null : responses.get(responses.size() - 1).getPost().getId();
        return new NoOffsetPage<>(responses, lastId, pageParam.getSize());
    }
}
