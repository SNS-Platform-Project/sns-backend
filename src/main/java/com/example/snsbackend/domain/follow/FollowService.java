package com.example.snsbackend.domain.follow;

import com.example.snsbackend.exception.ApiErrorType;
import com.example.snsbackend.exception.ApiException;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.mapper.*;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.*;
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
    private final ReceivedFollowRequestRepository receivedFollowRequestRepository;
    private final ReceivedFollowRequestMapper receivedFollowRequestMapper;
    private final SentFollowRequestRepository sentFollowRequestRepository;
    private final SentFollowRequestMapper sentFollowRequestMapper;

    // 팔로우
    public void follow(String followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Following following = followingRepository.findByUserId(userId);
        // 팔로잉 중복 확인
        if (following != null) {
            boolean alreadyFollowing = following.getFollowings().stream()
                    .anyMatch(existingFollow -> existingFollow.getFollowId().equals(followId));
            if (alreadyFollowing) {
                throw new ApiException(ApiErrorType.CONFLICT, "userId: " + followId, "이미 팔로우 중입니다. (following)");
            }
        }

        // 팔로잉 추가
        if (isPrivate(followId)) {
            // 팔로우 정보
            profileRepository.findById(followId)
                    .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "해당 계정을 찾지 못했습니다."));
            Follow follow = followMapper.toFollow(followId);

            // 팔로우 요청 목록 유무 확인 (없으면 생성)
            SentFollowRequest sentFollowRequest = sentFollowRequestRepository.findByUserId(userId);
            if (sentFollowRequest == null) {
                sentFollowRequest = sentFollowRequestMapper.toSentFollowRequest(userId);
                sentFollowRequest.setFollowings(new ArrayList<>());
            }

            // 팔로우 요청 중복 확인
            boolean alreadyFollowingRequest = sentFollowRequest.getFollowings().stream()
                    .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(followId));
            if (alreadyFollowingRequest) {
                throw new ApiException(ApiErrorType.CONFLICT, "userId: " + followId, "이미 팔로우 요청 중입니다.");
            }

            // 팔로잉 요청 목록에 추가
            sentFollowRequest.getFollowings().add(follow);
            sentFollowRequestRepository.save(sentFollowRequest);
        } else {
            following(userId, followId);
        }

        try {
            Follower follower = followerRepository.findByUserId(followId);
            if (follower != null) {
                // 팔로워 중복 확인
                boolean alreadyFollower = follower.getFollowers().stream()
                        .anyMatch(existingFollow -> existingFollow.getFollowId().equals(userId));
                if (alreadyFollower) {
                    throw new ApiException(ApiErrorType.CONFLICT, "userId: " + userId, "이미 팔로우 중입니다. (follower)");
                }
            }

            // 팔로워 추가
            if (isPrivate(followId)) {
                // 팔로우 정보
                Profile followProfile = profileRepository.findById(followId)
                        .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "해당 계정을 찾지 못했습니다."));
                Follow follow = followMapper.toFollow(userId);

                // 팔로우 요청 목록 유무 확인
                ReceivedFollowRequest receivedFollowRequest = receivedFollowRequestRepository.findByUserId(followId);
                if (receivedFollowRequest == null) {
                    receivedFollowRequest = receivedFollowRequestMapper.toFollowRequest(followId);
                    receivedFollowRequest.setFollowers(new ArrayList<>());
                }

                // 팔로우 요청 중복 확인
                boolean alreadyFollowerRequest = receivedFollowRequest.getFollowers().stream()
                        .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(userId));
                if (alreadyFollowerRequest) {
                    throw new ApiException(ApiErrorType.CONFLICT, "userId: " + userId, "이미 팔로우 요청 중입니다.");
                }

                // 팔로우 요청 목록에 추가
                receivedFollowRequest.getFollowers().add(follow);
                receivedFollowRequestRepository.save(receivedFollowRequest);
            } else {
                follower(userId, followId);
            }
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

        // 팔로잉 삭제
        unfollowing(userId, followId);

        try {
            // 팔로워 삭제
            unfollower(userId, followId);
        } catch (RuntimeException e) {
            following(userId, followId);
            throw new RuntimeException(e);
        }
    }

    // 팔로우 요청 수락
    public void acceptFollowRequest(String followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 받은 팔로우 요청 수락
        acceptReceivedFollowRequest(userId, followId);

        try {
            // 보낸 팔로우 요청 수락
            acceptSentFollowRequest(userId, followId);
        } catch (RuntimeException e) {
            unfollower(followId, userId);
            throw new RuntimeException(e);
        }
    }

    // 팔로우 요청 삭제
    public void rejectFollowRequest(String followId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        // 받은 팔로우 요청 삭제
        rejectReceivedFollowRequest(userId, followId);

        try {
            // 보낸 팔로우 요청 삭제
            rejectSentFollowRequest(userId, followId);
        } catch (RuntimeException e) {
            following(followId, userId);
            throw new RuntimeException(e);
        }
    }

    // 팔로워 목록
    public Follower follower() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        return followerRepository.findByUserId(userId);
    }

    // 팔로잉 목록
    public Following following() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        return followingRepository.findByUserId(userId);
    }

    // 팔로잉 추가
    private void following(String userId, String followId) {
        // 팔로우 정보
        profileRepository.findById(followId)
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "해당 계정을 찾지 못했습니다."));
        Follow follow = followMapper.toFollow(followId);

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
            throw new ApiException(ApiErrorType.CONFLICT, "userId: " + followId, "이미 팔로우 중입니다. (following)");
        }

        // 팔로잉 목록에 추가
        following.getFollowings().add(follow);
        followingRepository.save(following);

        // 팔로잉 수 증가
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다."));
        profile.setFollowingCount(profile.getFollowingCount() + 1);
        profileRepository.save(profile);
    }

    // 팔로워 추가
    private void follower(String userId, String followId) {
        // 팔로우 정보
        Profile followProfile = profileRepository.findById(followId)
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "해당 계정을 찾지 못했습니다."));
        Follow follow = followMapper.toFollow(userId);

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
            throw new ApiException(ApiErrorType.CONFLICT, "userId: " + userId, "이미 팔로우 중입니다. (follower)");
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
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로잉 목록을 찾을 수 없습니다. (following)");
        }

        // 팔로우 유무 확인
        boolean removed = following.getFollowings().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(followId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로잉 목록에서 찾을 수 없습니다. (following)");
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
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다. (following)"));
        profile.setFollowingCount(profile.getFollowingCount() - 1);
        profileRepository.save(profile);
    }

    // 팔로워 삭제
    private void unfollower(String userId,String followId) {
        // 팔로워 목록 유무 확인
        Follower follower = followerRepository.findByUserId(followId);
        if (follower == null) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로워 목록을 찾을 수 없습니다. (follower)");
        }

        // 팔로우 유무 확인
        boolean removed = follower.getFollowers().stream()
                .anyMatch(existingFollow -> existingFollow.getFollowId().equals(userId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로워 목록에서 찾을 수 없습니다. (follower)");
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
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "해당 계정을 찾지 못했습니다. (follower)"));
        followProfile.setFollowersCount(followProfile.getFollowersCount() - 1);
        profileRepository.save(followProfile);
    }

    // 계정 공개 여부
    private boolean isPrivate(String userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "해당 계정을 찾지 못했습니다."));
        return profile.isPrivate();
    }

    // 받은 팔로우 요청 수락
    private void acceptReceivedFollowRequest(String userId, String followId) {
        // 팔로우 요청 목록 유무 확인
        ReceivedFollowRequest receivedFollowRequest = receivedFollowRequestRepository.findByUserId(userId);
        if (receivedFollowRequest == null) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로우 요청 목록을 찾을 수 없습니다.");
        }

        // 팔로우 유무 확인
        boolean removed = receivedFollowRequest.getFollowers().stream()
                .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(followId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로우 요청 목록에서 찾을 수 없습니다.");
        }

        // 팔로우 요청 목록에서 삭제
        receivedFollowRequest.getFollowers().removeIf(existingFollowRequest -> existingFollowRequest.getFollowId().equals(followId));
        receivedFollowRequestRepository.save(receivedFollowRequest);

        // 팔로우 요청 목록 비었는지 확인
        if (receivedFollowRequest.getFollowers().isEmpty()) {
            receivedFollowRequestRepository.delete(receivedFollowRequest);
        }

        follower(followId, userId);
    }

    // 보낸 팔로우 요청 수락
    private void acceptSentFollowRequest(String userId, String followId) {
        // 팔로우 요청 목록 유무 확인
        SentFollowRequest sentFollowRequest = sentFollowRequestRepository.findByUserId(followId);
        if (sentFollowRequest == null) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로우 요청 목록을 찾을 수 없습니다.");
        }

        // 팔로우 유무 확인
        boolean removed = sentFollowRequest.getFollowings().stream()
                .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(userId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로우 요청 목록에서 찾을 수 없습니다.");
        }

        // 팔로우 요청 목록에서 삭제
        sentFollowRequest.getFollowings().removeIf(existingFollowRequest -> existingFollowRequest.getFollowId().equals(userId));
        sentFollowRequestRepository.save(sentFollowRequest);

        // 팔로우 요청 목록 비었는지 확인
        if (sentFollowRequest.getFollowings().isEmpty()) {
            sentFollowRequestRepository.delete(sentFollowRequest);
        }

        // 팔로잉 목록에 추가
        following(followId, userId);
    }

    // 받은 팔로우 요청 삭제
    private void rejectReceivedFollowRequest(String userId, String followId) {
        // 팔로우 요청 목록 유무 확인
        ReceivedFollowRequest receivedFollowRequest = receivedFollowRequestRepository.findByUserId(userId);
        if (receivedFollowRequest == null) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로우 요청 목록을 찾을 수 없습니다.");
        }

        // 팔로우 유무 확인
        boolean removed = receivedFollowRequest.getFollowers().stream()
                .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(followId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로우 요청 목록에서 찾을 수 없습니다.");
        }

        // 팔로우 요청 목록에서 삭제
        receivedFollowRequest.getFollowers().removeIf(existingFollowRequest -> existingFollowRequest.getFollowId().equals(followId));
        receivedFollowRequestRepository.save(receivedFollowRequest);

        // 팔로우 요청 목록 비었는지 확인
        if (receivedFollowRequest.getFollowers().isEmpty()) {
            receivedFollowRequestRepository.delete(receivedFollowRequest);
        }
    }

    // 보낸 팔로우 요청 삭제
    private void rejectSentFollowRequest(String userId, String followId) {
        // 팔로우 요청 목록 유무 확인
        SentFollowRequest sentFollowRequest = sentFollowRequestRepository.findByUserId(followId);
        if (sentFollowRequest == null) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + followId, "팔로우 요청 목록을 찾을 수 없습니다.");
        }

        // 팔로우 유무 확인
        boolean removed = sentFollowRequest.getFollowings().stream()
                .anyMatch(existingFollowRequest -> existingFollowRequest.getFollowId().equals(userId));
        if (!removed) {
            throw new ApiException(ApiErrorType.NOT_FOUND, "userId: " + userId, "팔로우 요청 목록에서 찾을 수 없습니다.");
        }

        // 팔로워 목록에서 삭제
        sentFollowRequest.getFollowings().removeIf(existingFollowRequest -> existingFollowRequest.getFollowId().equals(userId));
        sentFollowRequestRepository.save(sentFollowRequest);

        // 팔로워 목록 비었는지 확인
        if (sentFollowRequest.getFollowings().isEmpty()) {
            sentFollowRequestRepository.delete(sentFollowRequest);
        }
    }
}
