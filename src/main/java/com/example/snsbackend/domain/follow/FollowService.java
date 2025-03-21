package com.example.snsbackend.domain.follow;

import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.mapper.FollowMapper;
import com.example.snsbackend.mapper.FollowingMapper;
import com.example.snsbackend.model.Follow;
import com.example.snsbackend.model.Following;
import com.example.snsbackend.model.Profile;
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
    private final FollowMapper followMapper;
    private final FollowingMapper followingMapper;

    // 팔로우
    public void follow(String followName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 팔로잉 정보 가져오기
        Profile followProfile = profileRepository.findByUsername(followName)
                .orElseThrow(() -> new RuntimeException("Profile not found [username: " + followName + "]"));
        String followId = followProfile.getId();
        Follow follow = followMapper.toFollow(followId, followName);

        // 팔로잉 정보 저장
        Following following = followingRepository.findByUserId(userId);
        if (following == null) {
            following = followingMapper.toFollowing(userId);
            following.setFollowings(new ArrayList<>());
        }

        // 중복 확인
        boolean alreadyFollowing = following.getFollowings().stream()
                        .anyMatch(existingFollowing ->existingFollowing.getFollowId().equals(followId));

        if (alreadyFollowing) {
            throw new RuntimeException("Follow already exists [username: " + followName + "]");
        }

        following.getFollowings().add(follow);
        followingRepository.save(following);

        // 팔로잉 수 증가
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found [userId: " + userId + "]"));
        profile.setFollowingCount(profile.getFollowingCount() + 1);
        profileRepository.save(profile);
    }

    // 언팔로우
    public void unfollow(String followName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 팔로잉 삭제
        Following following = followingRepository.findByUserId(userId);
        boolean removed = following.getFollowings().removeIf(existingFollowing ->existingFollowing.getUsername().equals(followName));
        followingRepository.save(following);

        if (following.getFollowings().isEmpty()) {
            followingRepository.delete(following);
        }

        if (!removed) {
            throw new RuntimeException("Following not found [username: " + followName + "]");
        }

        // 팔로잉 수 감소
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found [userId: " + userId + "]"));
        profile.setFollowingCount(profile.getFollowingCount() - 1);
        profileRepository.save(profile);
    }
}
