package com.example.snsbackend.domain.follow;

import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.mapper.FollowMapper;
import com.example.snsbackend.mapper.FollowerMapper;
import com.example.snsbackend.mapper.FollowingMapper;
import com.example.snsbackend.model.Follow;
import com.example.snsbackend.model.Follower;
import com.example.snsbackend.model.Following;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.FollowerRepository;
import com.example.snsbackend.repository.FollowingRepository;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final ProfileRepository profileRepository;
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final FollowMapper followMapper;
    private final FollowingMapper followingMapper;
    private final FollowerMapper followerMapper;

    // 팔로우
    public void follow(String followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();
        String username = customUserDetails.getUsername();

        try {
            // 팔로잉 추가
            following(userId, followId);
        } catch (RuntimeException e) {
            unfollower(userId, followId);
            throw new RuntimeException(e);
        }

        try {
            // 팔로워 추가
            follower(userId, username, followId);
        } catch (RuntimeException e) {
            unfollowing(userId, followId);
            throw new RuntimeException(e);
        }
    }

    // 언팔로우
    public void unfollow(String followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        try {
            // 팔로잉 삭제
            unfollowing(userId, followId);
        } catch (RuntimeException e) {
            unfollower(userId, followId);
            throw new RuntimeException(e);
        }

        try {
            // 팔로워 삭제
            unfollower(userId, followId);
        } catch (RuntimeException e) {
            unfollowing(userId, followId);
            throw new RuntimeException(e);
        }
    }

    // 팔로잉 추가
    private void following(String userId, String followId) {
        // 팔로잉 목록 유무 확인 (없으면 생성)
        Following following = followingRepository.findByUserId(userId);
        if (following == null) {
            following = followingMapper.toFollowing(userId);
            following.setFollowings(new ArrayList<>());
        }

        // 팔로잉 중복 확인
        boolean alreadyFollowing = following.getFollowings().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(followId));
        if (alreadyFollowing) {
            throw new RuntimeException("Follow already exists (following) [userId: " + followId + "]");
        }

        // 팔로우 정보
        Profile followProfile = profileRepository.findById(followId)
                .orElseThrow(() -> new RuntimeException("Profile not found (following) [userId: " + followId + "]"));
        Follow follow = followMapper.toFollow(followId, followProfile.getUsername());

        // 팔로잉 목록에 추가
        following.getFollowings().add(follow);
        followingRepository.save(following);

        // 팔로잉 수 증가
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found (following) [userId: " + userId + "]"));
        profile.setFollowingCount(profile.getFollowingCount() + 1);
        profileRepository.save(profile);
    }

    // 팔로워 추가
    private void follower(String userId, String username, String followId) {
        // 팔로우 정보
        Profile followProfile = profileRepository.findById(followId)
                .orElseThrow(() -> new RuntimeException("Profile not found (follower) [userId: " + followId + "]"));
        Follow follow = followMapper.toFollow(userId, username);

        // 팔로워 목록 유무 확인
        Follower follower = followerRepository.findByUserId(followId);
        if (follower == null) {
            follower = followerMapper.toFollower(followId);
            follower.setFollowers(new ArrayList<>());
        }

        // 팔로워 중복 확인
        boolean alreadyFollower = follower.getFollowers().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(userId));
        if (alreadyFollower) {
            throw new RuntimeException("Follow already exists (follower) [userId: " + userId + "]");
        }

        // 팔로워 목록에 추가
        follower.getFollowers().add(follow);
        followerRepository.save(follower);

        // 팔로워 수 증가
        followProfile.setFollowersCount(followProfile.getFollowersCount() + 1);
        profileRepository.save(followProfile);
    }

    // 팔로잉 삭제
    private void unfollowing(String userId, String followId) {
        // 팔로잉 목록 유무 확인
        Following following = followingRepository.findByUserId(userId);
        if (following == null) {
            throw new RuntimeException("Following not found (following) [userId: " + userId + "]");
        }

        // 팔로우 유무 확인
        boolean removed = following.getFollowings().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(followId));
        if (!removed) {
            throw new RuntimeException("Follow not found (following) [userId: " + followId + "]");
        }

        // 팔로잉 목록에서 삭제
        following.getFollowings().removeIf(existingFollow -> existingFollow.getFollowId().equals(followId));
        followingRepository.save(following);

        // 팔로잉 목록 비었는지 확인
        if (following.getFollowings().isEmpty()) {
            followingRepository.delete(following);
        }

        // 팔로잉 수 감소
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found (following) [userId: " + userId + "]"));
        profile.setFollowingCount(profile.getFollowingCount() - 1);
        profileRepository.save(profile);
    }

    // 팔로워 삭제
    private void unfollower(String userId,String followId) {
        // 팔로워 목록 유무 확인
        Follower follower = followerRepository.findByUserId(followId);
        if (follower == null) {
            throw new RuntimeException("Follower not found (follower) [userId: " + followId + "]");
        }

        // 팔로우 유무 확인
        boolean removed = follower.getFollowers().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(userId));
        if (!removed) {
            throw new RuntimeException("Follow not found (follower) [userId: " + userId + "]");
        }

        // 팔로워 목록에서 삭제
        follower.getFollowers().removeIf(existingFollow -> existingFollow.getFollowId().equals(userId));
        followerRepository.save(follower);

        // 팔로워 목록 비었는지 확인
        if (follower.getFollowers().isEmpty()) {
            followerRepository.delete(follower);
        }

        // 팔로워 수 감소
        Profile followProfile = profileRepository.findById(followId)
                .orElseThrow(() -> new RuntimeException("Profile not found (follower) [userId: " + followId + "]"));
        followProfile.setFollowersCount(followProfile.getFollowersCount() - 1);
        profileRepository.save(followProfile);
    }
}
