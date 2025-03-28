package com.example.snsbackend.domain.feed;

import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.dto.PostResponse;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.*;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        // 사용자가 팔로우 중인 사람들 추출
        Following following = followingRepository.findByUserId(userId);
        if (following == null) {
            log.info("팔로우 중인 사람이 없습니다.");
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }
        List<String> follow = following.getFollowings().stream().map(Follow::getFollowId).toList();

        // 게시물 필터링 (팔로우 중인 사람들의 게시물)
        Query query = new Query().addCriteria(Criteria.where("user_id").in(follow));

        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 게시물 ID: {} 이후의 게시물을 조회합니다.", pageParam.getLastId());
        }

        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        List<Post> posts = mongoTemplate.find(query, Post.class);
        if (posts.isEmpty()) {
            log.info("팔로잉한 사람들 중 게시물을 작성한 사람이 없습니다.");
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }

        // PostResponse 정의
        List<PostResponse> postResponse = posts.stream().map(post -> {
            Optional<Profile> profile = profileRepository.findById(post.getUserId());
            if (profile.isEmpty()) {
                throw new RuntimeException("Profile not found [userId: " + userId + "]");
            }
            User user = new User(profile.get().getId(), profile.get().getUsername(), profile.get().getProfilePictureUrl());
            return new PostResponse(post, user);
        }).collect(Collectors.toList());

        return new NoOffsetPage<>(postResponse, postResponse.getLast().getPost().getId(), pageParam.getSize());
    }

    // 사용자 피드 조회
    public NoOffsetPage<PostResponse> getUserFeeds(PageParam pageParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 게시물 필터링 (팔로우 중인 사람들의 게시물)
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

        List<Post> posts = mongoTemplate.find(query, Post.class);
        if (posts.isEmpty()) {
            log.info("작성한 게시물이 없습니다. [userId: {}]", userId);
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }

        // PostResponse 정의
        List<PostResponse> postResponse = posts.stream().map(post -> {
            Optional<Profile> profile = profileRepository.findById(post.getUserId());
            if (profile.isEmpty()) {
                throw new RuntimeException("Profile not found [userId: " + userId + "]");
            }
            User user = new User(userId, profile.get().getUsername(), profile.get().getProfilePictureUrl());
            return new PostResponse(post, user);
        }).collect(Collectors.toList());

        return new NoOffsetPage<>(postResponse, postResponse.getLast().getPost().getId(), pageParam.getSize());
    }
}
